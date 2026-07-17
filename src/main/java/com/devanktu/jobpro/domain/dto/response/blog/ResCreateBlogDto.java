package com.devanktu.jobpro.domain.dto.response.blog;

import com.devanktu.jobpro.domain.Tag;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResCreateBlogDto {

    private Long id;
    private String title;
    private String content;
    private boolean active;
    private Instant createdAt;
    private String createdBy;

    private List<String> tags;


}


