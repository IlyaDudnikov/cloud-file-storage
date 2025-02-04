package com.ilyadudnikov.cloudfilestorage.controllers;

import com.ilyadudnikov.cloudfilestorage.dto.file.FileDto;
import com.ilyadudnikov.cloudfilestorage.dto.file.RenameFileDto;
import com.ilyadudnikov.cloudfilestorage.dto.file.UploadFileDto;
import com.ilyadudnikov.cloudfilestorage.security.CustomUserDetails;
import com.ilyadudnikov.cloudfilestorage.services.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public String uploadFile(@ModelAttribute("uploadFile") UploadFileDto uploadFile,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            uploadFile.setOwnerId(userDetails.getUser().getId());
            fileService.uploadFile(uploadFile);
            redirectAttributes.addFlashAttribute("success", "File uploaded successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "File upload failed");
        }

        redirectAttributes.addAttribute("path", uploadFile.getPath());
        return "redirect:/";
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> downloadFile(@ModelAttribute("fileDto") FileDto fileDto,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            fileDto.setOwnerId(userDetails.getUser().getId());
            ByteArrayResource byteArrayResource = fileService.downloadFile(fileDto);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileDto.getFileName())
                    .body(byteArrayResource); // Передаем InputStream клиенту
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping
    public String deleteFile(@ModelAttribute("fileDto") FileDto fileDto,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            fileDto.setOwnerId(userDetails.getUser().getId());
            fileService.deleteFile(fileDto);
            redirectAttributes.addFlashAttribute("success", "File deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "File deletion failed");
        }

        redirectAttributes.addAttribute("path", fileDto.getPath());
        return "redirect:/";
    }

    @PutMapping
    public String renameFile(@ModelAttribute("renameFileDto") RenameFileDto renameFileDto,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            renameFileDto.setOwnerId(userDetails.getUser().getId());
            fileService.renameFile(renameFileDto);
            redirectAttributes.addFlashAttribute("success", "File renamed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "File rename failed");
        }

        redirectAttributes.addAttribute("path", renameFileDto.getPath());
        return "redirect:/";
    }
}
