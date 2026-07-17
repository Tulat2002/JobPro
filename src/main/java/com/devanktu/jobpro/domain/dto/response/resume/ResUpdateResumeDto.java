package com.devanktu.jobpro.domain.dto.response.resume;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateResumeDto {

    private Instant updatedAt;
    private String updatedBy;

}
