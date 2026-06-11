package com.example.jobplatformsystem.dto.response;

import com.example.jobplatformsystem.entity.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {

    private Long id;

    private Long candidateId;

    private String candidateName;

    private Long jobId;

    private String jobTitle;

    private String coverLetter;

    private LocalDateTime appliedAt;

    private ApplicationStatus status;
}