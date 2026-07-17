package com.devanktu.jobpro.controller;

import com.devanktu.jobpro.domain.Comment;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.domain.dto.response.comment.ReqCommentDto;
import com.devanktu.jobpro.domain.dto.response.comment.ResCommentDto;
import com.devanktu.jobpro.domain.dto.response.comment.ResUpdateCommentDto;
import com.devanktu.jobpro.service.CommentService;
import com.devanktu.jobpro.utils.annotation.ApiMessage;
import com.devanktu.jobpro.utils.exception.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/comments")
    @ApiMessage("Create a comment")
    public ResponseEntity<ResCommentDto> createComment(@Valid @RequestBody ReqCommentDto reqCommentDto) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.commentService.createComment(reqCommentDto));
    }

    @GetMapping("/comments/blog/{id}")
    @ApiMessage("Fetch paginated parent comments with replies")
    public ResponseEntity<ResultPaginationDto> getCommentsByBlog(
            @PathVariable("id") long id,
            Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getCommentsByBlog(id, pageable));
    }

    @PutMapping("/comments/{id}")
    @ApiMessage("Update a comment")
    public ResponseEntity<ResUpdateCommentDto> updateComment(@RequestBody Map<String, String> payload,
                                                             @PathVariable("id") long id) throws IdInvalidException {
        String newContent = payload.get("content");
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IdInvalidException("Content cannot be empty");
        }
        return ResponseEntity.ok(commentService.updateComment(id, newContent));
    }

    @DeleteMapping("/comments/{id}")
    @ApiMessage("Delete a comment")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") long id) throws IdInvalidException {
        //check comment
        Comment comment = this.commentService.getCommentById(id);
        if (comment == null) {
            throw new IdInvalidException("Comment not found");
        }
        this.commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

}
