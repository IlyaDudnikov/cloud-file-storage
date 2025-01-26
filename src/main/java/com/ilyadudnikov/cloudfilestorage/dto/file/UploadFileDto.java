package com.ilyadudnikov.cloudfilestorage.dto.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadFileDto {
    private MultipartFile file;
    private String path;
    private Long ownerId;
}
