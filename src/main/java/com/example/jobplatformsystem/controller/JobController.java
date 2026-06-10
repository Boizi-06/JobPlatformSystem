package com.example.jobplatformsystem.controller;

import com.example.jobplatformsystem.dto.request.CreateJobRequest;
import com.example.jobplatformsystem.dto.request.UpdateJobRequest;
import com.example.jobplatformsystem.dto.response.JobResponse;
import com.example.jobplatformsystem.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public JobResponse createJob(
            @Valid @RequestBody
            CreateJobRequest request) {

        return jobService.createJob(request);
    }

    @GetMapping
    public List<JobResponse> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/{id}")
    public JobResponse getJobById(
            @PathVariable Long id) {

        return jobService.getJobById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteJob(
            @PathVariable Long id) {

        jobService.deleteJob(id);

        return "Job deleted successfully";
    }
    @PutMapping("/{id}")
    public JobResponse updateJob(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdateJobRequest request) {

        return jobService.updateJob(id, request);
    }
    @PutMapping("/{id}/approve")
    public JobResponse approveJob(
            @PathVariable Long id) {

        return jobService.approveJob(id);
    }
    @PutMapping("/{id}/reject")
    public JobResponse rejectJob(
            @PathVariable Long id) {

        return jobService.rejectJob(id);
    }
    @GetMapping("/search")
    public List<JobResponse> searchJobs(
            @RequestParam String keyword) {

        return jobService.searchJobs(keyword);
    }
    @GetMapping("/paging")
    public Page<JobResponse> getJobs(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size) {

        return jobService.getJobs(page, size);
    }
}