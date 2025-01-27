package com.ilyadudnikov.cloudfilestorage.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MinioObjectDto {
    private String name;
    private Boolean isDir;
    private String path;
}
