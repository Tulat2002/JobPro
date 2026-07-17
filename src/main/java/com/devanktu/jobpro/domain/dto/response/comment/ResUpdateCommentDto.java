package com.devanktu.jobpro.domain.dto.response.comment;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResUpdateCommentDto {
    private Long id;
    private String content;
    private String updatedBy;
    private Instant updatedAt;
}