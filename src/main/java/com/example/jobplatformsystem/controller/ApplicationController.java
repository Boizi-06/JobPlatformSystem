package com.example.jobplatformsystem.controller;

import com.example.jobplatformsystem.dto.request.ApplyJobRequest;
import com.example.jobplatformsystem.dto.response.ApplicationResponse;
import com.example.jobplatformsystem.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.jobplatformsystem.dto.request.UpdateApplicationStatusRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/apply")
    public ApplicationResponse applyJob(
            @Valid @RequestBody
            ApplyJobRequest request) {

        return applicationService
                .applyJob(request);
    }

    @GetMapping("/candidate/{candidateId}")
    public List<ApplicationResponse>
    getApplicationsByCandidate(
            @PathVariable Long candidateId) {

        return applicationService
                .getApplicationsByCandidate(
                        candidateId);
    }

    @GetMapping("/job/{jobId}")
    public List<ApplicationResponse>
    getApplicationsByJob(
            @PathVariable Long jobId) {

        return applicationService
                .getApplicationsByJob(jobId);
    }
    @PutMapping("/{id}/status")
    public ApplicationResponse updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateApplicationStatusRequest request) {

        return applicationService.updateStatus(
                id,
                request.getStatus()
        );
    }
}