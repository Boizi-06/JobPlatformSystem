package com.example.jobplatformsystem.service;

import com.example.jobplatformsystem.dto.request.ApplyJobRequest;
import com.example.jobplatformsystem.dto.response.ApplicationResponse;
import com.example.jobplatformsystem.entity.enums.ApplicationStatus;

import java.util.List;

public interface ApplicationService {

    ApplicationResponse applyJob(
            ApplyJobRequest request);

    List<ApplicationResponse> getApplicationsByCandidate(
            Long candidateId);

    List<ApplicationResponse> getApplicationsByJob(
            Long jobId);
    ApplicationResponse updateStatus(
            Long applicationId,
            ApplicationStatus status);
}