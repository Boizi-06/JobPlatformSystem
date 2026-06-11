package com.example.jobplatformsystem.service.impl;

import com.example.jobplatformsystem.entity.User;
import com.example.jobplatformsystem.exception.ResourceNotFoundException;
import com.example.jobplatformsystem.repository.UserRepository;
import com.example.jobplatformsystem.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl
        implements FileStorageService {

    private final UserRepository userRepository;

    @Override
    public String uploadCv(
            Long userId,
            MultipartFile file) {

        try {

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found"));

            String fileName =
                    System.currentTimeMillis()
                            + "_"
                            + file.getOriginalFilename();

            Path path = Paths.get(
                    "uploads/cv",
                    fileName
            );

            Files.createDirectories(
                    path.getParent()
            );

            Files.write(
                    path,
                    file.getBytes()
            );

            user.setCvUrl(
                    path.toString()
            );

            userRepository.save(user);

            return path.toString();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Upload failed"
            );
        }
    }
}