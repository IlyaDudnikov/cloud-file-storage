package com.ilyadudnikov.cloudfilestorage.utils;

import com.ilyadudnikov.cloudfilestorage.dto.BreadcrumbItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BreadcrumbUtils {
    public List<BreadcrumbItem> getBreadcrumbs(String path) {
        List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new BreadcrumbItem("Root", "/"));

        String[] parts = path.split("/");

        if (parts.length == 1 && parts[0].isEmpty()) {
            return breadcrumbs;
        }

        StringBuilder currentPath = new StringBuilder();
        for (String part : parts) {
            currentPath.append(part).append("/");
            breadcrumbs.add(new BreadcrumbItem(part, "/?path=" + currentPath));
        }

        return breadcrumbs;
    } 
}
