package com.example.jobplatformsystem.dto.response;

import com.example.jobplatformsystem.entity.enums.JobStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {

    private Long id;

    private String title;

    private String description;

    private Double salary;

    private String location;

    private LocalDate deadline;

    private JobStatus status;

    private Long employerId;

    private String employerUsername;
}