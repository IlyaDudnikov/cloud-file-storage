package com.ilyadudnikov.cloudfilestorage.controllers;

import com.ilyadudnikov.cloudfilestorage.dto.BreadcrumbItem;
import com.ilyadudnikov.cloudfilestorage.dto.MinioObjectDto;
import com.ilyadudnikov.cloudfilestorage.dto.file.FileDto;
import com.ilyadudnikov.cloudfilestorage.dto.file.RenameFileDto;
import com.ilyadudnikov.cloudfilestorage.dto.file.UploadFileDto;
import com.ilyadudnikov.cloudfilestorage.dto.folder.FolderDto;
import com.ilyadudnikov.cloudfilestorage.dto.folder.RenameFolderDto;
import com.ilyadudnikov.cloudfilestorage.dto.folder.UploadFolderDto;
import com.ilyadudnikov.cloudfilestorage.security.CustomUserDetails;
import com.ilyadudnikov.cloudfilestorage.services.FileService;
import com.ilyadudnikov.cloudfilestorage.services.SearchService;
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
    private final SearchService searchService;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "path", defaultValue = "", required = false) String path,
                        Model model) {
        if (userDetails != null) {
            List<MinioObjectDto> files = fileService.getUserFilesInFolder(userDetails.getUser().getId(), path);
            boolean hasFile = files.stream().anyMatch(file -> !file.getIsDir());
            boolean hasFolder = files.stream().anyMatch(MinioObjectDto::getIsDir);

            model.addAttribute("files", files);
            model.addAttribute("hasFile", hasFile);
            model.addAttribute("hasFolder", hasFolder);
        }
        model.addAttribute("folderDto", new FolderDto());
        model.addAttribute("renameFolderDto", new RenameFolderDto());

        model.addAttribute("renameFileDto", new RenameFileDto());
        model.addAttribute("fileDto", new FileDto());

        List<BreadcrumbItem> breadcrumbs = breadcrumbUtils.getBreadcrumbs(path);
        model.addAttribute("breadcrumbs", breadcrumbs);

        model.addAttribute("uploadFileDto", new UploadFileDto());
        model.addAttribute("uploadFolderDto", new UploadFolderDto());

        return "index";
    }


    @GetMapping("/search")
    public String search(@RequestParam("query") String query,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model) {
        List<MinioObjectDto> foundObjects = searchService.search(query, userDetails.getUser().getId());
        boolean hasFile = foundObjects.stream().anyMatch(file -> !file.getIsDir());
        boolean hasFolder = foundObjects.stream().anyMatch(MinioObjectDto::getIsDir);

        model.addAttribute("files", foundObjects);
        model.addAttribute("hasFile", hasFile);
        model.addAttribute("hasFolder", hasFolder);

        return "search";
    }

}
