package com.example.jobplatformsystem.service.impl;

import com.example.jobplatformsystem.dto.request.ApplyJobRequest;
import com.example.jobplatformsystem.dto.response.ApplicationResponse;
import com.example.jobplatformsystem.entity.Application;
import com.example.jobplatformsystem.entity.Job;
import com.example.jobplatformsystem.entity.User;
import com.example.jobplatformsystem.entity.enums.ApplicationStatus;
import com.example.jobplatformsystem.exception.ResourceNotFoundException;
import com.example.jobplatformsystem.repository.ApplicationRepository;
import com.example.jobplatformsystem.repository.JobRepository;
import com.example.jobplatformsystem.repository.UserRepository;
import com.example.jobplatformsystem.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl
        implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Override
    public ApplicationResponse applyJob(
            ApplyJobRequest request) {

        User candidate = userRepository
                .findById(request.getCandidateId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Candidate not found"));

        Job job = jobRepository
                .findById(request.getJobId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found"));

        Application application =
                Application.builder()
                        .candidate(candidate)
                        .job(job)
                        .coverLetter(
                                request.getCoverLetter())
                        .appliedAt(
                                LocalDateTime.now())
                        .status(
                                ApplicationStatus.PENDING)
                        .build();

        Application saved =
                applicationRepository.save(
                        application);

        return mapToResponse(saved);
    }

    @Override
    public List<ApplicationResponse>
    getApplicationsByCandidate(
            Long candidateId) {

        return applicationRepository
                .findByCandidateId(candidateId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ApplicationResponse>
    getApplicationsByJob(
            Long jobId) {

        return applicationRepository
                .findByJobId(jobId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ApplicationResponse mapToResponse(
            Application application) {

        return ApplicationResponse.builder()
                .id(application.getId())
                .candidateId(
                        application.getCandidate().getId())
                .candidateName(
                        application.getCandidate().getUsername())
                .jobId(
                        application.getJob().getId())
                .jobTitle(
                        application.getJob().getTitle())
                .coverLetter(
                        application.getCoverLetter())
                .appliedAt(
                        application.getAppliedAt())
                .status(
                        application.getStatus())
                .build();
    }
    @Override
    public ApplicationResponse updateStatus(
            Long applicationId,
            ApplicationStatus status) {

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found"));

        application.setStatus(status);

        Application updated =
                applicationRepository.save(application);

        return mapToResponse(updated);
    }
}