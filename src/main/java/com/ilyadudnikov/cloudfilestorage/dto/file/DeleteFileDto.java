package com.ilyadudnikov.cloudfilestorage.dto.file;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteFileDto {
    private String fileName;
    private String path;
    private Long ownerId;
}
