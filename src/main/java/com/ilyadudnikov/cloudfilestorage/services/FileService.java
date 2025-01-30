package com.ilyadudnikov.cloudfilestorage.services;

import com.ilyadudnikov.cloudfilestorage.dto.MinioObjectDto;
import com.ilyadudnikov.cloudfilestorage.dto.file.FileDto;
import com.ilyadudnikov.cloudfilestorage.dto.file.RenameFileDto;
import com.ilyadudnikov.cloudfilestorage.dto.file.UploadFileDto;
import com.ilyadudnikov.cloudfilestorage.exeptions.*;
import com.ilyadudnikov.cloudfilestorage.repositories.MinioRepository;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final MinioRepository minioRepository;

    public void uploadFile(UploadFileDto uploadFileDto) {
        MultipartFile file = uploadFileDto.getFile();
        try (InputStream inputStream = file.getInputStream()) {
            String fullFileName = getFullFileName(uploadFileDto.getOwnerId(),
                    uploadFileDto.getPath(),
                    file.getOriginalFilename());

            minioRepository.uploadFile(fullFileName, inputStream, file.getSize());

            log.info("File uploaded successfully");
        } catch (Exception e) {
            log.error("File was not uploaded");
            throw new FileWasNotUploadedException(e.getMessage());
        }
    }

    public List<MinioObjectDto> getUserFilesInFolder(long ownerId, String path) {
        String folderPath = getFolderPath(ownerId, path);
        Iterable<Result<Item>> results = minioRepository.getFiles(folderPath, false);

        List<MinioObjectDto> minioObjects = new ArrayList<>();
        results.forEach(result -> {
            try {
                Item item = result.get();
                String fullObjectName = item.objectName();
                String fileName = getObjectNameFromFullName(fullObjectName);

                minioObjects.add(
                        new MinioObjectDto(fileName, item.isDir(), path)
                );
            } catch (Exception e) {
                throw new FileOperationException(e.getMessage());
            }
        });

        return minioObjects;
    }

    public List<MinioObjectDto> getAllUserFilesInFolder(long ownerId, String path) {
        String folderPath = getFolderPath(ownerId, path);
        Iterable<Result<Item>> results = minioRepository.getFiles(folderPath, true);

        return convertToMinioObjects(results, ownerId);
    }

    public void deleteFile(FileDto fileDto) {
        String fullFileName = getFullFileName(fileDto.getOwnerId(), fileDto.getPath(), fileDto.getFileName());

        try {
            minioRepository.deleteFile(fullFileName);
            log.info("File deleted successfully");
        } catch (Exception e) {
            log.error("File was not deleted: {}", fileDto.getFileName(), e);
            throw new FileNotDeletedException(e.getMessage());
        }
    }

    public void renameFile(RenameFileDto renameFileDto) {
        String oldFullFileName = getFullFileName(renameFileDto.getOwnerId(),
                renameFileDto.getPath(),
                renameFileDto.getOldFileName());
        String newFullFileName = getFullFileName(renameFileDto.getOwnerId(),
                renameFileDto.getPath(),
                renameFileDto.getNewFileName());

        copyFileOrThrow(oldFullFileName, newFullFileName);

        try {
            minioRepository.deleteFile(oldFullFileName);
        } catch (Exception e) {
            log.error("Copied file was not deleted for rename: {}", oldFullFileName, e);
            throw new FileNotDeletedException(e.getMessage());
        }

        log.info("File renamed successfully");
    }

    public void copyFileOrThrow(String oldFullFileName, String newFullFileName) {
        try {
            minioRepository.copyFile(oldFullFileName, newFullFileName);
        } catch (Exception e) {
            log.error("File was not copied for rename: {}", oldFullFileName, e);
            throw new FileNotCopiedException(e.getMessage());
        }
    }

    public ByteArrayResource downloadFile(FileDto fileDto) {
        String fullFileName = getFullFileName(fileDto.getOwnerId(), fileDto.getPath(), fileDto.getFileName());
        try (InputStream inputStream = minioRepository.getFileInputStream(fullFileName)) {
            ByteArrayResource byteArrayResource = new ByteArrayResource(inputStream.readAllBytes());
            log.info("File downloaded successfully");
            return byteArrayResource;
        } catch (Exception e) {
            log.error("File was not downloaded: {}", fileDto.getFileName(), e);
            throw new FileNotDownloadedException(e.getMessage());
        }
    }

    private List<MinioObjectDto> convertToMinioObjects(Iterable<Result<Item>> results, long ownerId) {
        List<MinioObjectDto> minioObjects = new ArrayList<>();
        results.forEach(result -> {
            try {
                Item item = result.get();
                String fullObjectName = item.objectName();
                String objectPath = getObjectPathFromFullName(fullObjectName, ownerId);
                String objectName = getObjectNameFromFullName(fullObjectName);
                boolean isDir = fullObjectName.endsWith("/");

                minioObjects.add(
                        new MinioObjectDto(objectName, isDir, objectPath)
                );
            } catch (Exception e) {
                throw new FileOperationException(e.getMessage());
            }
        });

        return minioObjects;
    }

    private String getFolderPath(long ownerId, String path) {
        return "user-" + ownerId + "-files/" + path;
    }

    public String getFullFileName(long ownerId, String path, String filename) {
        return getFolderPath(ownerId, path) + filename;
    }

    private String getObjectNameFromFullName(String fullName) {
        if (fullName.endsWith("/")) {
            fullName = fullName.substring(0, fullName.length() - 1);
        }

        return fullName.substring(fullName.lastIndexOf("/") + 1);
    }

    private String getObjectPathFromFullName(String fullName, long ownerId) {
        if (fullName.endsWith("/")) {
            fullName = fullName.substring(0, fullName.length() - 1);
        }
        String ownerPrefix = "user-" + ownerId + "-files/";
        String objectPath = fullName.substring(0, fullName.lastIndexOf("/") + 1);
        return objectPath.replaceFirst(ownerPrefix, "");
    }

}
