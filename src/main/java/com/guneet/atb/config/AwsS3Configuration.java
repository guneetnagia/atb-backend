package com.guneet.atb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;

import java.time.Duration;


@Configuration
public class AwsS3Configuration {
    @Value("${aws.region}")
    private String awsRegion;
    @Bean
    public S3AsyncClient asyncClientS3() {

        S3AsyncClientBuilder builder =  S3AsyncClient.builder()
                .httpClient(sdkAsyncHttpClient())
                .region(Region.of(awsRegion))
                .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create());
        return builder.build();
    }

    private SdkAsyncHttpClient sdkAsyncHttpClient() {
        return NettyNioAsyncHttpClient.builder()
                .writeTimeout(Duration.ZERO)
                .maxConcurrency(64)
                .build();
    }
}
