package com.ilyadudnikov.cloudfilestorage.controllers;

import com.ilyadudnikov.cloudfilestorage.dto.BreadcrumbItem;
import com.ilyadudnikov.cloudfilestorage.dto.MinioObjectDto;
import com.ilyadudnikov.cloudfilestorage.dto.file.RenameFileDto;
import com.ilyadudnikov.cloudfilestorage.dto.folder.FolderDto;
import com.ilyadudnikov.cloudfilestorage.dto.folder.RenameFolderDto;
import com.ilyadudnikov.cloudfilestorage.security.CustomUserDetails;
import com.ilyadudnikov.cloudfilestorage.services.FileService;
import com.ilyadudnikov.cloudfilestorage.utils.BreadcrumbUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final FileService fileService;
    private final BreadcrumbUtils breadcrumbUtils;
    
    @GetMapping("/")
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "path", defaultValue = "", required = false) String path,
                        Model model) {
        if (userDetails != null) {
            List<MinioObjectDto> files = fileService.getUserFilesInFolder(userDetails.getUser().getId(), path);
            model.addAttribute("files", files);

        }
        model.addAttribute("folderDto", new FolderDto());
        model.addAttribute("renameFolderDto", new RenameFolderDto());

        model.addAttribute("renameFileDto", new RenameFileDto());

        List<BreadcrumbItem> breadcrumbs = breadcrumbUtils.getBreadcrumbs(path);
        model.addAttribute("breadcrumbs", breadcrumbs);

        return "index";
    }


}
