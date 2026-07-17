package com.devanktu.jobpro.domain.dto.response.blog;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResUpdateBlogDto {

    private Long id;
    private String title;
    private String content;
    private boolean active;
    private Instant updatedAt;
    private String updatedBy;

    private List<String> tags;

}
