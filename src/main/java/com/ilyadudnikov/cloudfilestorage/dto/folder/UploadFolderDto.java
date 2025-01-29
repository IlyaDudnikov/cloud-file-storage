package com.ilyadudnikov.cloudfilestorage.dto.folder;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UploadFolderDto {
    private List<MultipartFile> files;
    private String path;
    private long ownerId;
}
