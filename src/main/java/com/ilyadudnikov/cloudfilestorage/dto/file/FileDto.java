package com.ilyadudnikov.cloudfilestorage.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileDto {
    private String fileName;
    private String path;
    private Long ownerId;
}
