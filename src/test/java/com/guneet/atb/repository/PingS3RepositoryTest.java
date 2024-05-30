package com.guneet.atb.repository;


import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.Notifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class PingS3RepositoryTest {
    
    private final S3AsyncClient s3AsyncClient = mock(S3AsyncClient.class);
    private final Notifier notifier = mock(Notifier.class);
    private static final String bucketName = "some_bucket";
    
    private final ArgumentCaptor<Notice> noticeArgumentCaptor = ArgumentCaptor.forClass(Notice.class);
    
    private PingS3Repository pingS3Repository;

    @BeforeEach
    void setUp() {
        pingS3Repository = new PingS3Repository(s3AsyncClient, bucketName, notifier);
    }

    @Test
    void testCreatePing() {
        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
                .thenReturn(CompletableFuture.completedFuture(PutObjectResponse.builder().build()));
        Mono<Void> createPing = pingS3Repository.create("serial_number");
        StepVerifier.create(createPing).verifyComplete();

        verify(s3AsyncClient, times(1)).putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class));
        verifyNoMoreInteractions(s3AsyncClient);
    }    @Test
    void testDeletePing() {
        when(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(DeleteObjectResponse.builder().build()));
        Mono<Void> deletePing = pingS3Repository.delete("serial_number");
        StepVerifier.create(deletePing).verifyComplete();

        verify(s3AsyncClient, times(1)).deleteObject(any(DeleteObjectRequest.class));
        verifyNoMoreInteractions(s3AsyncClient);
    }

    @Test
    void testRetryBucketCreation() {

        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
                .thenReturn(CompletableFuture.failedFuture(NoSuchBucketException.builder().build()))
                .thenReturn(CompletableFuture.completedFuture(PutObjectResponse.builder().build()));
        when(s3AsyncClient.createBucket(any(CreateBucketRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(CreateBucketResponse.builder().build()));

        Mono<Void> createPing = pingS3Repository.create("serial_number");
        StepVerifier.create(createPing)
                .verifyComplete();

        // Verify that putObject was called twice, indicating a successful retry after bucket creation
        verify(s3AsyncClient, times(2)).putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class));
        verify(s3AsyncClient, times(1)).createBucket(any(CreateBucketRequest.class));
        verifyNoMoreInteractions(s3AsyncClient);
    }

    @Test
    void testCreatePingFailure() {
        Notice notice = new Notice();
        when(notifier.buildNotice(any(Exception.class))).thenReturn(notice);
        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException()));

        Mono<Void> createPing = pingS3Repository.create("serial_number");
        StepVerifier.create(createPing)
                .verifyComplete();

        verify(notifier).buildNotice(any(Exception.class));
        verify(notifier).send(noticeArgumentCaptor.capture());
        Notice value = noticeArgumentCaptor.getValue();
        assertEquals("Device::Ping", value.params.get("component"));
        assertEquals(bucketName, value.params.get("bucket"));
        assertEquals("serial_number", value.params.get("key"));
        assertEquals("create_ping", value.params.get("action"));
    }

    @Test
    void testDeletePingFailure() {
        Notice notice = new Notice();
        when(notifier.buildNotice(any(Exception.class))).thenReturn(notice);
        when(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException()));

        Mono<Void> deletePingMono = pingS3Repository.delete("serial_number");
        StepVerifier.create(deletePingMono)
                .verifyComplete();

        verify(notifier).buildNotice(any(Exception.class));
        verify(notifier).send(noticeArgumentCaptor.capture());
        Notice value = noticeArgumentCaptor.getValue();
        assertEquals("Device::Ping", value.params.get("component"));
        assertEquals(bucketName, value.params.get("bucket"));
        assertEquals("serial_number", value.params.get("key"));
        assertEquals("delete_ping", value.params.get("action"));
    }
}
