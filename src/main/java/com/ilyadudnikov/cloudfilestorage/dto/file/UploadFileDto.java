package com.ilyadudnikov.cloudfilestorage.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UploadFileDto {
    private MultipartFile file;
    private String path;
    private Long ownerId;
}
