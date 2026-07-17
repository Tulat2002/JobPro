package com.devanktu.jobpro.service;

import com.devanktu.jobpro.domain.Blog;
import com.devanktu.jobpro.domain.Comment;
import com.devanktu.jobpro.domain.User;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.domain.dto.response.comment.CommentDto;
import com.devanktu.jobpro.domain.dto.response.comment.ReqCommentDto;
import com.devanktu.jobpro.domain.dto.response.comment.ResCommentDto;
import com.devanktu.jobpro.domain.dto.response.comment.ResUpdateCommentDto;
import com.devanktu.jobpro.repository.BlogRepository;
import com.devanktu.jobpro.repository.CommentRepository;
import com.devanktu.jobpro.utils.SecurityUtil;
import com.devanktu.jobpro.utils.exception.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, BlogRepository blogRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
        this.userService = userService;
    }

    public ResultPaginationDto getCommentsByBlog(Long blogId, Pageable pageable) {
        Page<Comment> parentComments = commentRepository.findByBlogIdAndParentIsNull(blogId, pageable);

        List<CommentDto> parentDtos = parentComments.getContent().stream()
                .map(this::convertToDtoWithReplies)
                .collect(Collectors.toList());

        ResultPaginationDto result = new ResultPaginationDto();

        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(parentComments.getTotalPages());
        meta.setTotal(parentComments.getTotalElements());

        result.setMeta(meta);
        result.setResult(parentDtos);

        return result;
    }

    private CommentDto convertToDtoWithReplies(Comment comment) {
        List<CommentDto> replyDtos = comment.getReplies() != null
                ? comment.getReplies().stream()
                .map(reply -> CommentDto.builder()
                        .id(reply.getId())
                        .content(reply.getContent())
                        .userName(reply.getUser() != null ? reply.getUser().getName() : null)
                        .userEmail(reply.getUser() != null ? reply.getUser().getEmail() : null)
                        .parentId(reply.getParent() != null ? reply.getParent().getId() : null)
                        .build())
                .collect(Collectors.toList())
                : new ArrayList<>();

        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userName(comment.getUser() != null ? comment.getUser().getName() : null)
                .userEmail(comment.getUser() != null ? comment.getUser().getEmail() : null)
                .parentId(null)
                .replies(replyDtos)
                .build();
    }

    public ResCommentDto createComment(ReqCommentDto reqCommentDto) throws IdInvalidException {
        //check user
        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        if (email == null)
            throw new IdInvalidException("User not authenticated.");
        User currentUser = this.userService.handleGetUserByUsername(email);
        Blog blog = this.blogRepository.findById(reqCommentDto.getBlogId())
                .orElseThrow(() -> new IdInvalidException("Blog not found with id: " + reqCommentDto.getBlogId()));

        Comment comment = new Comment();
        comment.setContent(reqCommentDto.getContent());
        comment.setBlog(blog);
        comment.setUser(currentUser);
        comment.setCreatedAt(Instant.now());
        comment.setCreatedBy(currentUser.getEmail());

        if (reqCommentDto.getParentId() != null) {
            Comment parent = commentRepository.findById(reqCommentDto.getParentId())
                    .orElseThrow(() -> new IdInvalidException("Parent comment not found"));
            comment.setParent(parent);
        }
        Comment savedComment = this.commentRepository.save(comment);
        return this.convertToComment(savedComment);
    }

    public ResCommentDto convertToComment(Comment comment){
        //thong tin user viet comment
        ResCommentDto.UserComment userComment = null;
        if (comment.getUser() != null) {
            userComment = ResCommentDto.UserComment.builder()
                    .id(comment.getUser().getId())
                    .email(comment.getUser().getEmail())
                    .name(comment.getUser().getName())
                    .build();
        }
        //Map user is exist
        List<ResCommentDto.ReplyDto> replies = null;
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            replies = comment.getReplies()
                    .stream().map(rep -> ResCommentDto.ReplyDto.builder()
                            .id(rep.getId())
                            .content(rep.getContent())
                            .createdAt(rep.getCreatedAt())
                            .user(ResCommentDto.UserComment.builder()
                                    .id(rep.getUser().getId())
                                    .name(rep.getUser().getName())
                                    .email(rep.getUser().getEmail())
                                    .build())
                            .build()).collect(Collectors.toList());
        }
        return ResCommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .createdBy(comment.getCreatedBy())
                .updatedBy(comment.getUpdatedBy())
                .user(userComment)
                .replies(replies)
                .build();
    }

    public ResUpdateCommentDto updateComment(Long commentId, String newContent) {
        //get user in context
        String email = SecurityUtil.getCurrentUserLogin().orElse(null);

        User currentUser = this.userService.handleGetUserByUsername(email);

        //check comment
        Optional<Comment> optionalComment = this.commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setContent(newContent);
            comment.setUpdatedAt(Instant.now());
            comment.setUpdatedBy(currentUser.getEmail());
            comment = this.commentRepository.save(comment);
            return ResUpdateCommentDto.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .updatedAt(comment.getUpdatedAt())
                    .updatedBy(comment.getUpdatedBy())
                    .build();
        }
        return null;
    }

    public void deleteComment(long id) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(
                () -> new IdInvalidException("User not authenticated"));
        User currentUser = this.userService.handleGetUserByUsername(email);

        Optional<Comment> optionalComment = this.commentRepository.findById(id);
        if (optionalComment.isPresent() &&
                optionalComment.get().getUser().getId() == currentUser.getId()) {
            this.commentRepository.delete(optionalComment.get());
        }
    }

    public Comment getCommentById(long id) {
        Optional<Comment> optionalComment = this.commentRepository.findById(id);
        if (optionalComment.isPresent()) {
            return optionalComment.get();
        }
        return null;
    }


}
