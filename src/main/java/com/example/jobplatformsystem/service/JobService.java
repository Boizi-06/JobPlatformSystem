package com.example.jobplatformsystem.service;

import com.example.jobplatformsystem.dto.request.CreateJobRequest;
import com.example.jobplatformsystem.dto.request.UpdateJobRequest;
import com.example.jobplatformsystem.dto.response.JobResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JobService {

    JobResponse createJob(CreateJobRequest request);

    List<JobResponse> getAllJobs();

    JobResponse getJobById(Long id);

    void deleteJob(Long id);
    JobResponse updateJob(
            Long id,
            UpdateJobRequest request);

    JobResponse approveJob(Long id);
    JobResponse rejectJob(Long id);
    List<JobResponse> searchJobs(String keyword);


    Page<JobResponse> getJobs(
            int page,
            int size);
}