package com.example.jobplatformsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateJobRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Salary is required")
    private Double salary;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Deadline is required")
    private LocalDate deadline;
}