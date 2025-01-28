package com.ilyadudnikov.cloudfilestorage.dto.file;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameFileDto {
    private String oldFileName;
    private String newFileName;
    private String path;
    private long ownerId;
}
