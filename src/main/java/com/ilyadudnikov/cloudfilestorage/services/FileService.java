package com.ilyadudnikov.cloudfilestorage.services;

import com.ilyadudnikov.cloudfilestorage.dto.MinioObjectDto;
import com.ilyadudnikov.cloudfilestorage.dto.file.DeleteFileDto;
import com.ilyadudnikov.cloudfilestorage.dto.file.UploadFileDto;
import com.ilyadudnikov.cloudfilestorage.exeptions.FileNotDeletedException;
import com.ilyadudnikov.cloudfilestorage.exeptions.FileOperationException;
import com.ilyadudnikov.cloudfilestorage.exeptions.FileWasNotUploadedException;
import com.ilyadudnikov.cloudfilestorage.repositories.MinioRepository;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public List<MinioObjectDto> getUserFilesInFolder(long ownerId, String path, boolean recursive) {
        String folderPath = getFolderPath(ownerId, path);
        Iterable<Result<Item>> results = minioRepository.getFiles(folderPath, recursive);

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

    public void deleteFile(DeleteFileDto deleteFileDto) {
        String fullFileName = getFullFileName(deleteFileDto.getOwnerId(), deleteFileDto.getPath(), deleteFileDto.getFileName());

        try {
            minioRepository.deleteObject(fullFileName);
            log.info("File deleted successfully");
        } catch (Exception e) {
            log.error("File was not deleted: {}", deleteFileDto.getFileName(), e);
            throw new FileNotDeletedException(e.getMessage());
        }
    }

//    public void renameFile(String oldName, String newName, long ownerId, String path) {
//        String fullFileName = getFullFileName(ownerId, oldName, path);
//
//    }

    String getFolderPath(long ownerId, String path) {
        return "user-" + ownerId + "-files/" + path;
    }

    String getFullFileName(long ownerId, String path, String filename) {
        return getFolderPath(ownerId, path) + filename;
    }

    String getObjectNameFromFullName(String fullName) {
        if (fullName.endsWith("/")) {
            fullName = fullName.substring(0, fullName.length() - 1);
        }

        return fullName.substring(fullName.lastIndexOf("/") + 1);
    }

}
