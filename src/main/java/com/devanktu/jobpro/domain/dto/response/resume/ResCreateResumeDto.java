package com.devanktu.jobpro.domain.dto.response.resume;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateResumeDto {

    private long id;
    private Instant createdAt;
    private String createdBy;

}
