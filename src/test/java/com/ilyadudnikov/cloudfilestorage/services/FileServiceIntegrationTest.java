package com.ilyadudnikov.cloudfilestorage.services;

import com.ilyadudnikov.cloudfilestorage.config.minio.MinioProperties;
import com.ilyadudnikov.cloudfilestorage.dto.file.FileDto;
import com.ilyadudnikov.cloudfilestorage.dto.file.UploadFileDto;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
class FileServiceIntegrationTest {

    @Container
    static MinIOContainer minioContainer = new MinIOContainer("minio/minio:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("minio.client.endpoint", minioContainer::getS3URL);
        registry.add("minio.client.access-key", minioContainer::getUserName);
        registry.add("minio.client.secret-key", minioContainer::getPassword);
    }

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private FileService fileService;

    @Autowired
    private MinioProperties minioProperties;

    @Test
    void testUploadFile() {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());

        UploadFileDto uploadFileDto = new UploadFileDto(mockFile, "folder/", 1L);

        assertDoesNotThrow(() -> fileService.uploadFile(uploadFileDto));
    }

    @Test
    void testGetUserFilesInFolder() throws Exception {
        // Заливаем файл вручную
        String testFileName = "user-1-files/folder/test.txt";
        try (InputStream inputStream = new ByteArrayInputStream("Hello World".getBytes())) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(testFileName)
                    .stream(inputStream, -1, 10485760)
                    .build());
        }

        List<?> files = fileService.getUserFilesInFolder(1L, "folder/");

        assertEquals(1, files.size());
    }

    @Test
    void testDeleteFile() throws Exception {
        String testFileName = "user-1-files/folder/delete.txt";
        try (InputStream inputStream = new ByteArrayInputStream("To be deleted".getBytes())) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(testFileName)
                    .stream(inputStream, -1, 10485760)
                    .build());
        }

        FileDto fileDto = new FileDto("delete.txt", "folder/", 1L);

        assertDoesNotThrow(() -> fileService.deleteFile(fileDto));
    }

    @Test
    void testDownloadFile() throws Exception {
        String testFileName = "user-1-files/folder/download.txt";
        try (InputStream inputStream = new ByteArrayInputStream("Download me".getBytes())) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(testFileName)
                    .stream(inputStream, -1, 10485760)
                    .build());
        }

        FileDto fileDto = new FileDto("download.txt", "folder/", 1L);

        ByteArrayResource fileResource = fileService.downloadFile(fileDto);

        assertNotNull(fileResource);
        assertEquals("Download me", new String(fileResource.getByteArray()));
    }

}
