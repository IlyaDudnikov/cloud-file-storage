package com.ilyadudnikov.cloudfilestorage.services;

import com.ilyadudnikov.cloudfilestorage.dto.MinioObjectDto;
import com.ilyadudnikov.cloudfilestorage.dto.folder.FolderDto;
import com.ilyadudnikov.cloudfilestorage.dto.folder.RenameFolderDto;
import com.ilyadudnikov.cloudfilestorage.dto.folder.UploadFolderDto;
import com.ilyadudnikov.cloudfilestorage.exeptions.FolderNotDeletedException;
import com.ilyadudnikov.cloudfilestorage.exeptions.FolderNotDownloadedException;
import com.ilyadudnikov.cloudfilestorage.exeptions.FolderNotUploadedException;
import com.ilyadudnikov.cloudfilestorage.repositories.MinioRepository;
import io.minio.Result;
import io.minio.SnowballObject;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FolderService {

    private final MinioRepository minioRepository;
    private final FileService fileService;

    public void uploadFolder(UploadFolderDto uploadFolderDto) {
        List<SnowballObject> objects = new ArrayList<>();

        for (MultipartFile file : uploadFolderDto.getFiles()) {
            String fullFileName = fileService.getFullFileName(uploadFolderDto.getOwnerId(),
                    uploadFolderDto.getPath(), file.getOriginalFilename());

            try (InputStream inputStream = file.getInputStream()) {
                objects.add(
                        new SnowballObject(
                                fullFileName,
                                inputStream,
                                file.getSize(),
                                null));
            } catch (Exception e) {
                log.error("Error while uploading file: {}", file.getOriginalFilename(), e);
                throw new FolderNotUploadedException(e.getMessage());
            }
        }

        try {
            minioRepository.uploadFolder(objects);
        } catch (Exception e) {
            log.error("Error while uploading folder: {}", uploadFolderDto.getPath(), e);
            throw new FolderNotUploadedException(e.getMessage());
        }
    }

    public void downloadFolder(FolderDto folderDto, OutputStream outputStream) {
        String fullPath = getFolderPath(folderDto);
        List<MinioObjectDto> allUserFilesInFolder = fileService.getAllUserFilesInFolder(folderDto.getOwnerId(), fullPath);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            for (MinioObjectDto file : allUserFilesInFolder) {
                String fullFileName = fileService.getFullFileName(
                        folderDto.getOwnerId(), file.getPath(), file.getName());
                InputStream fileInputStream = minioRepository.getFileInputStream(fullFileName);
                zipOutputStream.putNextEntry(new ZipEntry(file.getPath() + file.getName()));

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    zipOutputStream.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
                zipOutputStream.closeEntry();
            }
            log.info("Files are archived for downloading");
        } catch (Exception e) {
            log.error("The files were not archived for downloading", e);
            throw new FolderNotDownloadedException(e.getMessage());
        }
    }

    public void deleteFolder(FolderDto folderDto) {
        String folderPath = getFolderPath(folderDto);
        List<MinioObjectDto> userFilesInFolder = fileService.getAllUserFilesInFolder(folderDto.getOwnerId(), folderPath);

        deleteFiles(userFilesInFolder, folderDto);
    }

    public void renameFolder(RenameFolderDto renameFolderDto) {
        FolderDto folderDto = new FolderDto(renameFolderDto.getOldFolderName(),
                renameFolderDto.getPath(), renameFolderDto.getOwnerId());
        String folderPath = getFolderPath(folderDto);
        List<MinioObjectDto> userFilesInFolder = fileService.getAllUserFilesInFolder(folderDto.getOwnerId(), folderPath);

        for (MinioObjectDto file : userFilesInFolder) {
            String oldFullFileName = fileService.getFullFileName(renameFolderDto.getOwnerId(),
                    file.getPath(), file.getName());

            String newFolderPath = getFolderPath(renameFolderDto.getPath(), renameFolderDto.getNewFolderName());
            String newFilePath = file.getPath().replaceFirst(folderPath, newFolderPath);

            String newFullFileName = fileService.getFullFileName(renameFolderDto.getOwnerId(),
                    newFilePath, file.getName());

            fileService.copyFileOrThrow(oldFullFileName, newFullFileName);
        }

        deleteFiles(userFilesInFolder, folderDto);

        log.info("Folder successfully renamed");
    }

    private void deleteFiles(List<MinioObjectDto> files, FolderDto folderDto) {
        List<DeleteObject> deleteFiles = convertToDeleteObjects(files, folderDto.getOwnerId());
        try {
            Iterable<Result<DeleteError>> results = minioRepository.deleteFiles(deleteFiles);

            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("Error in deleting object {}; {}", error.objectName(), error.message());
            }
        } catch (Exception e) {
            log.error("Error while deleting folder: {}", getFolderPath(folderDto), e);
            throw new FolderNotDeletedException(e.getMessage());
        }
    }
    
    private String getFolderPath(FolderDto folderDto) {
        return folderDto.getPath() +
                folderDto.getFolderName() + "/";
    }

    private String getFolderPath(String path, String folderName) {
        return path +
                folderName + "/";
    }

    private List<DeleteObject> convertToDeleteObjects(List<MinioObjectDto> minioObjects, long ownerId) {
        List<DeleteObject> deleteObjects = new LinkedList<>();
        for (MinioObjectDto file : minioObjects) {
            String fullFileName = fileService.getFullFileName(ownerId, file.getPath(), file.getName());
            deleteObjects.add(new DeleteObject(fullFileName));
        }

        return deleteObjects;
    }
}
