package com.guneet.atb.repository;


import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.Notifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

@Slf4j
@Repository

public class PingS3Repository implements PingRepository {

    private final S3AsyncClient s3AsyncClient;
    private final String bucketName;
    private final Notifier airbrake;

    public PingS3Repository(S3AsyncClient s3AsyncClient,
                            @Value("aws.s3.ping.bucket") String bucketName, Notifier airbrake) {
        this.s3AsyncClient = s3AsyncClient;
        this.bucketName = bucketName;
        this.airbrake = airbrake;
    }

    @Override
    public Mono<Void> create(String serialNumber) {
        return createFile(serialNumber)
                .onErrorResume(NoSuchBucketException.class, error -> createBucketAndFile(serialNumber))
                .onErrorResume(e -> {
                    notifyAirbrake(serialNumber, e, "create_ping");
                    return Mono.empty();
                }).then();
    }

    @Override
    public Mono<Void> delete(String serialNumber) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(serialNumber)
                .build();

        Mono<DeleteObjectResponse> deletePing = Mono.fromFuture(() -> s3AsyncClient.deleteObject(deleteObjectRequest))
                .onErrorResume(e -> {
                    notifyAirbrake(serialNumber, e, "delete_ping");
                    return Mono.empty();
                });
        return deletePing.then();
    }

    private Mono<PutObjectResponse> createFile(String serialNumber) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(serialNumber)
                .acl("public-read")
                .contentType("text/plain")
                .cacheControl("no-cache")
                .contentDisposition("inline")
                .build();
        AsyncRequestBody requestBody = AsyncRequestBody.fromString(serialNumber);
        return Mono.fromFuture(() -> s3AsyncClient.putObject(putObjectRequest, requestBody));
    }

    private Mono<PutObjectResponse> createBucketAndFile(String serialNumber) {
        CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();
        return Mono.fromFuture(() -> s3AsyncClient.createBucket(createBucketRequest))
                .then(createFile(serialNumber));
    }

    private void notifyAirbrake(String serialNumber, Throwable exception, String action) {
        log.error(ExceptionUtils.getStackTrace(exception));
        log.error("Failed to {} for {}", action, serialNumber);
        Notice notice = airbrake.buildNotice(exception);
        notice.setParam("component", "Device::Ping");
        notice.setParam("bucket", bucketName);
        notice.setParam("key", serialNumber);
        notice.setParam("action", action);
        airbrake.send(notice);
    }
}
