package com.ilyadudnikov.cloudfilestorage.dto.folder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameFolderDto {
    private String oldFolderName;
    private String newFolderName;
    private String path;
    private long ownerId;
}