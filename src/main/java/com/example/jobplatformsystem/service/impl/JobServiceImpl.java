package com.example.jobplatformsystem.service.impl;

import com.example.jobplatformsystem.dto.request.CreateJobRequest;
import com.example.jobplatformsystem.dto.request.UpdateJobRequest;
import com.example.jobplatformsystem.dto.response.JobResponse;
import com.example.jobplatformsystem.entity.Job;
import com.example.jobplatformsystem.entity.User;
import com.example.jobplatformsystem.entity.enums.JobStatus;
import com.example.jobplatformsystem.exception.ResourceNotFoundException;
import com.example.jobplatformsystem.mapper.JobMapper;
import com.example.jobplatformsystem.repository.JobRepository;
import com.example.jobplatformsystem.repository.UserRepository;
import com.example.jobplatformsystem.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public JobResponse createJob(CreateJobRequest request) {

        User employer = userRepository.findById(
                        request.getEmployerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employer not found"));

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .salary(request.getSalary())
                .location(request.getLocation())
                .deadline(request.getDeadline())
                .status(JobStatus.PENDING)
                .employer(employer)
                .build();

        return JobMapper.toResponse(
                jobRepository.save(job));
    }

    @Override
    public List<JobResponse> getAllJobs() {

        return jobRepository.findAll()
                .stream()
                .map(JobMapper::toResponse)
                .toList();
    }

    @Override
    public JobResponse getJobById(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found"));

        return JobMapper.toResponse(job);
    }

    @Override
    public void deleteJob(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found"));

        jobRepository.delete(job);
    }
    @Override
    public JobResponse updateJob(
            Long id,
            UpdateJobRequest request) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found"));

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setSalary(request.getSalary());
        job.setLocation(request.getLocation());
        job.setDeadline(request.getDeadline());

        return JobMapper.toResponse(
                jobRepository.save(job));
    }
    @Override
    public JobResponse approveJob(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found"));

        job.setStatus(JobStatus.APPROVED);

        return JobMapper.toResponse(
                jobRepository.save(job));
    }
    @Override
    public JobResponse rejectJob(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found"));

        job.setStatus(JobStatus.REJECTED);

        return JobMapper.toResponse(
                jobRepository.save(job));
    }
    @Override
    public List<JobResponse> searchJobs(String keyword) {

        return jobRepository
                .findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(JobMapper::toResponse)
                .toList();
    }
    @Override
    public Page<JobResponse> getJobs(
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return jobRepository.findAll(pageable)
                .map(JobMapper::toResponse);
    }
}