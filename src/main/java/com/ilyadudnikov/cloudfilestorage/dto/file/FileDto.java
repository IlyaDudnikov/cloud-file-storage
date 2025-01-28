package com.ilyadudnikov.cloudfilestorage.dto.file;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDto {
    private String fileName;
    private String path;
    private Long ownerId;
}
