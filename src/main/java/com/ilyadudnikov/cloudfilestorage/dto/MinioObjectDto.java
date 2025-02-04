package com.ilyadudnikov.cloudfilestorage.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MinioObjectDto {
    private String name;
    private Boolean isDir;
    private String path;
}
