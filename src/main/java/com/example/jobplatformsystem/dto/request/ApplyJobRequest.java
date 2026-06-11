package com.example.jobplatformsystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyJobRequest {

    @NotNull
    private Long candidateId;

    @NotNull
    private Long jobId;

    private String coverLetter;
}