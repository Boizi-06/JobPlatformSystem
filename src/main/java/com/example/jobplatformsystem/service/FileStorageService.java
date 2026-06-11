package com.example.jobplatformsystem.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String uploadCv(
            Long userId,
            MultipartFile file);
}