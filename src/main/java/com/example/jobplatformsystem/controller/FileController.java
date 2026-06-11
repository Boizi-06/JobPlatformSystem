package com.example.jobplatformsystem.controller;

import com.example.jobplatformsystem.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload-cv/{userId}")
    public String uploadCv(
            @PathVariable Long userId,
            @RequestParam("file")
            MultipartFile file) {

        return fileStorageService
                .uploadCv(userId, file);
    }
}