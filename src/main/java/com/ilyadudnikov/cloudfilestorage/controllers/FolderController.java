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
    public String uploadFolder(@ModelAttribute("uploadFolder") UploadFolderDto uploadFolderDto,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            uploadFolderDto.setOwnerId(userDetails.getUser().getId());
            folderService.uploadFolder(uploadFolderDto);
            redirectAttributes.addFlashAttribute("success", "Folder uploaded successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Folder upload failed");
        }

        redirectAttributes.addAttribute("path", uploadFolderDto.getPath());
        return "redirect:/";
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFolder(@ModelAttribute("folderDto") FolderDto folderDto,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               HttpServletResponse response) {
        try {
            folderDto.setOwnerId(userDetails.getUser().getId());

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
    public String createFolder(@ModelAttribute("folderDto") FolderDto folderDto,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            folderDto.setOwnerId(userDetails.getUser().getId());
            folderService.createFolder(folderDto);
            redirectAttributes.addFlashAttribute("success", "Folder created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while creating the folder");
        }

        redirectAttributes.addFlashAttribute("path", folderDto.getPath());
        return "redirect:/";
    }

    @DeleteMapping
    public String deleteFolder(@ModelAttribute("folderDto") FolderDto folderDto,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            folderDto.setOwnerId(userDetails.getUser().getId());
            folderService.deleteFolder(folderDto);

            redirectAttributes.addFlashAttribute("success", "Folder deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting a folder");
        }

        redirectAttributes.addAttribute("path", folderDto.getPath());
        return "redirect:/";
    }

    @PutMapping
    public String renameFolder(@ModelAttribute("renameFolderDto") RenameFolderDto renameFolderDto,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            renameFolderDto.setOwnerId(userDetails.getUser().getId());
            folderService.renameFolder(renameFolderDto);
            redirectAttributes.addFlashAttribute("success", "Folder renamed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while renaming the folder");
        }

        redirectAttributes.addAttribute("path", renameFolderDto.getPath());
        return "redirect:/";
    }
}
