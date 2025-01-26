package com.ilyadudnikov.cloudfilestorage.services;

import com.ilyadudnikov.cloudfilestorage.dto.file.UploadFileDto;
import com.ilyadudnikov.cloudfilestorage.exeptions.FileWasNotUploadedException;
import com.ilyadudnikov.cloudfilestorage.repositories.MinioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final MinioRepository minioRepository;

    public void uploadFile(UploadFileDto uploadFileDto) {
        MultipartFile file = uploadFileDto.getFile();
        try (InputStream inputStream = file.getInputStream()) {
            String fullFileName = "user-" + uploadFileDto.getOwnerId() + "-files/"
                    + uploadFileDto.getPath()
                    + file.getOriginalFilename();

            minioRepository.uploadFile(fullFileName, inputStream, file.getSize());

            log.info("File uploaded successfully");
        } catch (Exception e) {
            log.error("File was not uploaded");
            throw new FileWasNotUploadedException(e.getMessage());
        }
    }

}
