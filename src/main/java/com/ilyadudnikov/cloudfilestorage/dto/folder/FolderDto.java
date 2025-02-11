package com.ilyadudnikov.cloudfilestorage.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FolderDto {
    private String folderName;
    private String path;
    private long ownerId;
}
