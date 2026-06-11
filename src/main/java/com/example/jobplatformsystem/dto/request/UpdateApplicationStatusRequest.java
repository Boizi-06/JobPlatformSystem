package com.example.jobplatformsystem.dto.request;

import com.example.jobplatformsystem.entity.enums.ApplicationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateApplicationStatusRequest {

    private ApplicationStatus status;
}