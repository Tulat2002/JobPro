package com.devanktu.jobpro.domain.dto.response.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqCommentDto {

    @NotBlank(message = "Nội dung comment không được để trống")
    private String content;

    @NotNull(message = "Blog ID không được để trống")
    private Long blogId;

    private Long parentId; // Nếu là reply thì có parentId, nếu comment gốc thì null
}