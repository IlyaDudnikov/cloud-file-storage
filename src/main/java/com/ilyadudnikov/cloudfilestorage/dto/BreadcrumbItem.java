package com.ilyadudnikov.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BreadcrumbItem {
    private String text;
    private String link;
}
