package com.devanktu.jobpro.controller;

import com.devanktu.jobpro.domain.Tag;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.service.TagService;
import com.devanktu.jobpro.utils.annotation.ApiMessage;
import com.devanktu.jobpro.utils.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("/tags")
    @ApiMessage("Create a tag")
    public ResponseEntity<Tag> createTag(@Valid @RequestBody Tag tag) throws IdInvalidException {
        //check exist by name
        boolean isExist = this.tagService.existsByName(tag.getName());
        if (isExist) {
            throw new IdInvalidException("Tag already exists with name " + tag.getName() + " please use another tag.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.tagService.handleCreateTag(tag));
    }

    @PutMapping("/tags")
    @ApiMessage("Update a tag")
    public ResponseEntity<Tag> updateTag(@Valid @RequestBody Tag tag) throws IdInvalidException {
        //check id
        Tag currentTag = this.tagService.fetchTagById(tag.getId());
        if (currentTag == null) {
            throw new IdInvalidException("Tag not found with id " + tag.getId());
        }
        //check name
        boolean isExist = this.tagService.existsByName(tag.getName());
        if (isExist) {
            throw new IdInvalidException("Tag already exists with name " + tag.getName());
        }
        currentTag.setName(tag.getName());
        return ResponseEntity.ok().body(this.tagService.handleUpdateTag(tag));
    }

    @GetMapping("/tags/{id}")
    @ApiMessage("Fetch tag by id")
    public ResponseEntity<Tag> getTagById(@PathVariable("id") long id) throws IdInvalidException {
        //check id
        Tag tag = this.tagService.fetchTagById(id);
        if (tag != null) {
            return ResponseEntity.ok().body(tag);
        }else {
            throw new IdInvalidException("Tag not found with id " + id);
        }
    }

    @DeleteMapping("/tags/{id}")
    @ApiMessage("Delete a tag")
    public ResponseEntity<Void> deleteTag(@PathVariable("id") long id) throws IdInvalidException {
        //check id
        Tag idTag = this.tagService.fetchTagById(id);
        if (idTag == null) {
            throw new IdInvalidException("Tag with " + id + " not found.");
        }
        this.tagService.deleteTagById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tags")
    @ApiMessage("Fetch tag panigation and filter")
    public ResponseEntity<ResultPaginationDto> getAllTag(@Filter Specification<Tag> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.tagService.getAllTag(spec, pageable));
    }

}
