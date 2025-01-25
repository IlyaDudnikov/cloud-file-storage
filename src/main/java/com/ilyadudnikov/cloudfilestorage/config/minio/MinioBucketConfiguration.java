package com.ilyadudnikov.cloudfilestorage.config.minio;

import com.ilyadudnikov.cloudfilestorage.exeptions.BucketWasNotCreatedException;
import com.ilyadudnikov.cloudfilestorage.services.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
@RequiredArgsConstructor
public class MinioBucketConfiguration {

    private final MinioProperties minioProperties;
    private final BucketService bucketService;

    @EventListener(ContextRefreshedEvent.class)
    public void createBucketIfNotExist() {
        try {
            if (!bucketService.isBucketExist()) {
                bucketService.createBucket();
            }
        } catch (Exception e) {
            throw new BucketWasNotCreatedException("Bucket " + minioProperties.getBucketName() + " was not created");
        }
    }
}
