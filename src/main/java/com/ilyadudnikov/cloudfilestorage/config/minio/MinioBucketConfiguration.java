package com.ilyadudnikov.cloudfilestorage.config.minio;

import com.ilyadudnikov.cloudfilestorage.services.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
@RequiredArgsConstructor
public class MinioBucketConfiguration {
    @Value("${minio.bucket-name}")
    private String bucketName;

    private final BucketService bucketService;

    @EventListener(ContextRefreshedEvent.class)
    public void createBucketIfNotExist() {
        try {
            if (!bucketService.isBucketExist(bucketName)) {
                bucketService.createBucket(bucketName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Bucket " + bucketName + " was not created", e);
        }
    }
}
