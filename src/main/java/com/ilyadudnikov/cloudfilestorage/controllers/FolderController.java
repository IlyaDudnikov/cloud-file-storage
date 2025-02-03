package com.ilyadudnikov.cloudfilestorage.controllers;

import com.ilyadudnikov.cloudfilestorage.dto.folder.FolderDto;
import com.ilyadudnikov.cloudfilestorage.dto.folder.RenameFolderDto;
import com.ilyadudnikov.cloudfilestorage.dto.folder.UploadFolderDto;
import com.ilyadudnikov.cloudfilestorage.security.CustomUserDetails;
import com.ilyadudnikov.cloudfilestorage.services.FolderService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/folders")
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/upload")
    public String uploadFolder(@ModelAttribute("uploadFolder") UploadFolderDto uploadFolderDto) {
        uploadFolderDto.setOwnerId(3L);
        folderService.uploadFolder(uploadFolderDto);
        return "index1";
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFolder(HttpServletResponse response) {
        try {

            FolderDto folderDto = new FolderDto();
            folderDto.setFolderName("folder-new");
            folderDto.setPath("folder-1/");
            folderDto.setOwnerId(3L);

            // Устанавливаем заголовки для ответа
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=" + folderDto.getFolderName() + ".zip");

            // Передаем OutputStream из HttpServletResponse в ваш сервис
            folderService.downloadFolder(folderDto, response.getOutputStream());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось скачать папку", e);
        }
    }

    @PostMapping
    public String createFolder1(@ModelAttribute("folderDto") FolderDto folderDto,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        return "index1";
    }

    @DeleteMapping
    public String deleteFolder(@ModelAttribute("folderDto") FolderDto folderDto,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        folderDto.setOwnerId(userDetails.getUser().getId());
        folderService.deleteFolder(folderDto);

        redirectAttributes.addAttribute("path", folderDto.getPath());
        redirectAttributes.addFlashAttribute("success", "Folder deleted successfully!");
        return "redirect:/";
    }

    @PutMapping
    public String renameFolder(@ModelAttribute("renameFolderDto") RenameFolderDto renameFolderDto,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        renameFolderDto.setOwnerId(userDetails.getUser().getId());
        folderService.renameFolder(renameFolderDto);
        redirectAttributes.addAttribute("path", renameFolderDto.getPath());
        redirectAttributes.addFlashAttribute("success", "Folder renamed successfully!");
        return "redirect:/";
    }

    @PostMapping("/rename")
    public String renameFolder1() {
        RenameFolderDto renameFolderDto = new RenameFolderDto();
        renameFolderDto.setNewFolderName("folder-new");
        renameFolderDto.setOldFolderName("2");
        renameFolderDto.setOwnerId(3L);
        renameFolderDto.setPath("folder-1/");
        folderService.renameFolder(renameFolderDto);
        return "index1";
    }



    @PostMapping("/create")
    public String createFolder() {
        FolderDto newFolderDto = new FolderDto();
        newFolderDto.setFolderName("folder-new");
        newFolderDto.setPath("folder-1/2/");
        newFolderDto.setOwnerId(3L);
        folderService.createFolder(newFolderDto);
        return "index1";
    }
}
