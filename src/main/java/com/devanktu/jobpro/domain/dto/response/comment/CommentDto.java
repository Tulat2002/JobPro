package com.devanktu.jobpro.domain.dto.response.comment;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private String userName;
    private String userEmail;
    private Long parentId;
    private List<CommentDto> replies;
}
