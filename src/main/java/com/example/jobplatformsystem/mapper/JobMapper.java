package com.example.jobplatformsystem.mapper;

import com.example.jobplatformsystem.dto.response.JobResponse;
import com.example.jobplatformsystem.entity.Job;

public class JobMapper {

    public static JobResponse toResponse(Job job) {

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .salary(job.getSalary())
                .location(job.getLocation())
                .deadline(job.getDeadline())
                .status(job.getStatus())
                .employerId(job.getEmployer().getId())
                .employerUsername(job.getEmployer().getUsername())
                .build();
    }
}