package com.devanktu.jobpro.controller;

import com.devanktu.jobpro.domain.Blog;
import com.devanktu.jobpro.domain.Job;
import com.devanktu.jobpro.domain.User;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.domain.dto.response.blog.ResBlogDto;
import com.devanktu.jobpro.domain.dto.response.blog.ResCreateBlogDto;
import com.devanktu.jobpro.domain.dto.response.blog.ResUpdateBlogDto;
import com.devanktu.jobpro.service.BlogService;
import com.devanktu.jobpro.service.UserService;
import com.devanktu.jobpro.utils.SecurityUtil;
import com.devanktu.jobpro.utils.annotation.ApiMessage;
import com.devanktu.jobpro.utils.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class BlogController {

    private final BlogService blogService;
    private final UserService userService;

    public BlogController(BlogService blogService, UserService userService) {
        this.blogService = blogService;
        this.userService = userService;
    }

    @PostMapping("/blogs")
    @ApiMessage("Create new blog")
    public ResponseEntity<ResCreateBlogDto> createBlog(@Valid @RequestBody Blog blog) throws IdInvalidException {
        //check user
        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        if (email == null)
            throw new IdInvalidException("User not authenticated.");
        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser == null)
            throw new IdInvalidException("User not found with email: " + email);
        // Gán user cho blog
        blog.setUser(currentUser);
        blog.setCreatedBy(currentUser.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(this.blogService.createBlogDto(blog));
    }

    @PutMapping("/blogs")
    @ApiMessage("Update a blog")
    public ResponseEntity<ResUpdateBlogDto> updateBlog(@Valid @RequestBody Blog blog) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.OK).body(this.blogService.updateBlogDto(blog));
    }

    @GetMapping("/blogs/{id}")
    @ApiMessage("Get blog by id")
    public ResponseEntity<ResBlogDto> getBlogById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Blog> optionalBlog = this.blogService.fetchBlogById(id);
        if (!optionalBlog.isPresent())
            throw new IdInvalidException("Blog not found");
        Blog blog = optionalBlog.get();
        return ResponseEntity.status(HttpStatus.OK).body(this.blogService.convertToBlogDto(blog));
    }

    @DeleteMapping("/blogs/{id}")
    @ApiMessage("Delete a blog by id")
    public ResponseEntity<Void> deleteBlogById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Blog> optionalBlog = this.blogService.fetchBlogById(id);
        if (!optionalBlog.isPresent())
            throw new IdInvalidException("Blog not found");
        //delete
        this.blogService.deleteBlogById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/blogs")
    @ApiMessage("Fetch all blog panigation and filter")
    public ResponseEntity<ResultPaginationDto> getAllBlogs(@Filter Specification<Blog> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.blogService.fetchAllBlogs(spec, pageable));
    }

}
