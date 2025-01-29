package com.ilyadudnikov.cloudfilestorage.dto.folder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderDto {
    private String folderName;
    private String path;
    private long ownerId;
}
