package com.devanktu.jobpro.service;

import com.devanktu.jobpro.domain.Blog;
import com.devanktu.jobpro.domain.Job;
import com.devanktu.jobpro.domain.Tag;
import com.devanktu.jobpro.domain.User;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.domain.dto.response.blog.ResBlogDto;
import com.devanktu.jobpro.domain.dto.response.blog.ResCreateBlogDto;
import com.devanktu.jobpro.domain.dto.response.blog.ResUpdateBlogDto;
import com.devanktu.jobpro.repository.BlogRepository;
import com.devanktu.jobpro.repository.TagRepository;
import com.devanktu.jobpro.utils.SecurityUtil;
import com.devanktu.jobpro.utils.exception.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserService userService;
    private final TagRepository tagRepository;

    public BlogService(BlogRepository blogRepository, UserService userService, TagRepository tagRepository) {
        this.blogRepository = blogRepository;
        this.userService = userService;
        this.tagRepository = tagRepository;
    }

    public ResCreateBlogDto createBlogDto(Blog blog) {
        //check tag
        if (blog.getTags() != null){
            List<Long> requestTag = blog.getTags()
                    .stream().map(tag -> tag.getId()).collect(Collectors.toList());
            List<Tag> currentTag = this.tagRepository.findByIdIn(requestTag);
            blog.setTags(currentTag);
        }
        //create blog
        Blog savedBlog = this.blogRepository.save(blog);

        //convert to response
        ResCreateBlogDto resCreateBlogDto = new ResCreateBlogDto();
        resCreateBlogDto.setId(savedBlog.getId());
        resCreateBlogDto.setTitle(savedBlog.getTitle());
        resCreateBlogDto.setContent(savedBlog.getContent());
        resCreateBlogDto.setActive(savedBlog.isActive());
        resCreateBlogDto.setCreatedAt(savedBlog.getCreatedAt());
        resCreateBlogDto.setCreatedBy(savedBlog.getCreatedBy());
        if (savedBlog.getTags() != null){
            List<String> tags = savedBlog.getTags()
                    .stream().map(t -> t.getName()).collect(Collectors.toList());
            resCreateBlogDto.setTags(tags);
        }
        return resCreateBlogDto;
    }

    public ResUpdateBlogDto updateBlogDto(Blog blog) throws IdInvalidException {
        //check user
        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        if (email == null) {
            throw new IdInvalidException("User not authentication");
        }
        //check permission
        User currentUser = this.userService.handleGetUserByUsername(email);

        Blog existingBlog = this.blogRepository.findById(blog.getId())
                .orElseThrow(() -> new IdInvalidException("Blog not found with id: " + blog.getId()));

        // Kiểm tra quyền sở hữu
        if (existingBlog.getUser() == null || !Objects.equals(existingBlog.getUser().getId(), currentUser.getId())) {
            throw new IdInvalidException("You do not have permission to update this blog");
        }

        // Cập nhật các field được phép
        existingBlog.setTitle(blog.getTitle());
        existingBlog.setContent(blog.getContent());
        existingBlog.setActive(blog.isActive());
        existingBlog.setUpdatedBy(currentUser.getEmail());
        // Cập nhật tag (nếu có)
        if (blog.getTags() != null && !blog.getTags().isEmpty()) {
            List<Long> requestTagIds = blog.getTags()
                    .stream().map(Tag::getId).collect(Collectors.toList());
            List<Tag> foundTags = this.tagRepository.findByIdIn(requestTagIds);
            existingBlog.setTags(foundTags);
        }

        //save blog
        Blog savedBlog = this.blogRepository.save(existingBlog);

        //convert to response
        ResUpdateBlogDto resUpdateBlogDto = new ResUpdateBlogDto();
        resUpdateBlogDto.setId(savedBlog.getId());
        resUpdateBlogDto.setTitle(savedBlog.getTitle());
        resUpdateBlogDto.setContent(savedBlog.getContent());
        resUpdateBlogDto.setActive(savedBlog.isActive());
        resUpdateBlogDto.setUpdatedAt(savedBlog.getUpdatedAt());
        resUpdateBlogDto.setUpdatedBy(savedBlog.getUpdatedBy());
        if (savedBlog.getTags() != null) {
            List<String> tagNames = savedBlog.getTags()
                    .stream().map(Tag::getName).collect(Collectors.toList());
            resUpdateBlogDto.setTags(tagNames);
        }
        return resUpdateBlogDto;
    }

    public Optional<Blog> fetchBlogById(long id) {
        return this.blogRepository.findById(id);
    }

    public ResBlogDto convertToBlogDto(Blog blog) {
        ResBlogDto resBlogDto = new ResBlogDto();
        resBlogDto.setId(blog.getId());
        resBlogDto.setTitle(blog.getTitle());
        resBlogDto.setContent(blog.getContent());
        resBlogDto.setActive(blog.isActive());
        resBlogDto.setViews(blog.getView());

        if (blog.getUser() != null) {
            ResBlogDto.UserBlog userBlog = new ResBlogDto.UserBlog(
                    blog.getUser().getId(),
                    blog.getUser().getName(),
                    blog.getUser().getEmail()
            );
            resBlogDto.setUserBlog(userBlog);
        }

        return resBlogDto;
    }

    public ResBlogDto convertToBlogDtos(Blog blog) {
        ResBlogDto resBlogDto = ResBlogDto.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .active(blog.isActive())
                .views(blog.getView())
                .userBlog(new ResBlogDto.UserBlog(
                        blog.getUser().getId(),
                        blog.getUser().getName(),
                        blog.getUser().getEmail()
                ))
                .build();
        return resBlogDto;
    }

    public void deleteBlogById(long id) {
        this.blogRepository.deleteById(id);
    }

    public ResultPaginationDto fetchAllBlogs(Specification<Blog> spec, Pageable pageable) {
        Page<Blog> pageBlog = this.blogRepository.findAll(spec, pageable);
        ResultPaginationDto result = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageBlog.getTotalPages());
        meta.setTotal(pageBlog.getTotalElements());
        result.setMeta(meta);
        result.setResult(pageBlog.getContent());
        return result;
    }

}
