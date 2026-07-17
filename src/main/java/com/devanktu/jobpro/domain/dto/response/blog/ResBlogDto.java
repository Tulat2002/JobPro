package com.devanktu.jobpro.domain.dto.response.blog;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResBlogDto {

    private Long id;
    private String title;
    private String content;
    private boolean active;
    private long views;
    private UserBlog userBlog;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserBlog {
        private Long id;
        private String name;
        private String email;
    }

}
