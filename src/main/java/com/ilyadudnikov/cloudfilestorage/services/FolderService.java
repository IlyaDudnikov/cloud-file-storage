package com.ilyadudnikov.cloudfilestorage.services;

import com.ilyadudnikov.cloudfilestorage.dto.MinioObjectDto;
import com.ilyadudnikov.cloudfilestorage.dto.folder.FolderDto;
import com.ilyadudnikov.cloudfilestorage.exeptions.FolderNotDownloadedException;
import com.ilyadudnikov.cloudfilestorage.repositories.MinioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FolderService {

    private final MinioRepository minioRepository;
    private final FileService fileService;

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
    
    private String getFolderPath(FolderDto folderDto) {
        return folderDto.getPath() +
                folderDto.getFolderName() + "/";
    }
}
