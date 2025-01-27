package com.ilyadudnikov.cloudfilestorage.repositories;

import com.ilyadudnikov.cloudfilestorage.config.minio.MinioProperties;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MinioRepository {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    private String bucketName;

    @PostConstruct
    private void init() {
        bucketName = minioProperties.getBucketName();
    }

    public boolean isBucketExists() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );
    }

    public void createBucket() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.makeBucket(
                MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
        );
    }

    public void uploadFile(String fileFullName, InputStream inputStream, long objectSize) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileFullName)
                        .stream(inputStream, objectSize, -1)
                        .build()
        );
    }

    public Iterable<Result<Item>> getFiles(String path, boolean recursive) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(recursive)
                        .prefix(path)
                        .build()
        );
    }

    public void deleteObject(String objectFullName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectFullName)
                        .build()
        );
    }
}
