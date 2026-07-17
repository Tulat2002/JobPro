package com.devanktu.jobpro.service;

import com.devanktu.jobpro.domain.Skill;
import com.devanktu.jobpro.domain.Tag;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag handleCreateTag(Tag tag) {
        return this.tagRepository.save(tag);
    }

    public Tag fetchTagById(Long id) {
        Optional<Tag> optionalTag = this.tagRepository.findById(id);
        if (optionalTag.isPresent()){
            return optionalTag.get();
        }
        return null;
    }

    public List<Tag> fetchAllTags() {
        return this.tagRepository.findAll();
    }

    public Tag handleUpdateTag(Tag tag){
        Tag currentTag = this.fetchTagById(tag.getId());
        if (currentTag != null){
            currentTag.setName(tag.getName());
            currentTag.setDescription(tag.getDescription());
            currentTag.setSlug(tag.getSlug());
            currentTag = this.tagRepository.save(currentTag);
            return currentTag;
        }
        return null;
    }

    public void deleteTagById(Long id) {
        //get tah in blog_tag table and remove tag in blog
        Optional<Tag> tagOptional = this.tagRepository.findById(id);
        Tag currentTags = tagOptional.get();
        currentTags.getBlogs().forEach(blog -> blog.getTags().remove(currentTags));

        //delete tag
        this.tagRepository.delete(currentTags);
    }

    public ResultPaginationDto getAllTag(Specification<Tag> spec, Pageable pageable){
        Page<Tag> pageTag = this.tagRepository.findAll(spec, pageable);
        ResultPaginationDto result = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageTag.getTotalPages());
        meta.setTotal(pageTag.getTotalElements());

        result.setMeta(meta);
        result.setResult(pageTag.getContent());
        return result;
    }

    public boolean existsByName(String name){
        return this.tagRepository.existsByName(name);
    }

}
