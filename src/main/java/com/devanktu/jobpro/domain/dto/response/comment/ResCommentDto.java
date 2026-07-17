package com.devanktu.jobpro.domain.dto.response.comment;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResCommentDto {
    private Long id;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private UserComment user; // thông tin người viết comment
    private List<ReplyDto> replies; // danh sách reply con (nếu có)

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserComment {
        private Long id;
        private String name;
        private String email;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ReplyDto {
        private Long id;
        private String content;
        private Instant createdAt;
        private UserComment user;
    }
}