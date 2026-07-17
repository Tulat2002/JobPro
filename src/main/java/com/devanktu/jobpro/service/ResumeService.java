package com.devanktu.jobpro.service;

import com.devanktu.jobpro.domain.Job;
import com.devanktu.jobpro.domain.Resume;
import com.devanktu.jobpro.domain.User;
import com.devanktu.jobpro.domain.dto.response.resume.ResCreateResumeDto;
import com.devanktu.jobpro.domain.dto.response.resume.ResFetchResumeDto;
import com.devanktu.jobpro.domain.dto.response.resume.ResUpdateResumeDto;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.repository.JobRepository;
import com.devanktu.jobpro.repository.ResumeRepository;
import com.devanktu.jobpro.repository.UserRepository;
import com.devanktu.jobpro.utils.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    @Autowired
    FilterBuilder filterBuilder;

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ResumeService(ResumeRepository resumeRepository, JobRepository jobRepository, UserRepository userRepository) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public Optional<Resume> fetchById(long id){
        return this.resumeRepository.findById(id);
    }

    public boolean checkResumeExistByUserAndJob(Resume resume){
        //check user by id
        if (resume.getUser() == null)
            return false;
        Optional<User> optionalUser = this.userRepository.findById(resume.getUser().getId());
        if (optionalUser.isEmpty())
            return false;
        //check job by id
        if (resume.getJob() == null)
            return false;
        Optional<Job> optionalJob = this.jobRepository.findById(resume.getJob().getId());
        if (optionalJob.isEmpty())
            return false;
        return true;
    }

    public ResCreateResumeDto createResume(Resume resume){
        this.resumeRepository.save(resume);
        //convert
        ResCreateResumeDto resCreateResumeDto = new ResCreateResumeDto();
        resCreateResumeDto.setId(resume.getId());
        resCreateResumeDto.setCreatedAt(resume.getCreatedAt());
        resCreateResumeDto.setCreatedBy(resume.getCreatedBy());
        return resCreateResumeDto;
    }

    public ResUpdateResumeDto updateResume(Resume resume){
        this.resumeRepository.save(resume);
        //convert
        ResUpdateResumeDto resUpdateResumeDto = new ResUpdateResumeDto();
        resUpdateResumeDto.setUpdatedAt(resume.getUpdatedAt());
        resUpdateResumeDto.setUpdatedBy(resume.getUpdatedBy());
        return resUpdateResumeDto;
    }

    public void deleteResume(long id){
        this.resumeRepository.deleteById(id);
    }

    public ResFetchResumeDto getResume(Resume resume){
        ResFetchResumeDto resFetchResumeDto = new ResFetchResumeDto();
        resFetchResumeDto.setId(resume.getId());
        resFetchResumeDto.setEmail(resume.getEmail());
        resFetchResumeDto.setUrl(resume.getUrl());
        resFetchResumeDto.setStatus(resume.getStatus());
        resFetchResumeDto.setCreatedAt(resume.getCreatedAt());
        resFetchResumeDto.setCreatedBy(resume.getCreatedBy());
        resFetchResumeDto.setUpdatedAt(resume.getUpdatedAt());
        resFetchResumeDto.setUpdatedBy(resume.getUpdatedBy());
        if (resume.getJob() != null) {
            resFetchResumeDto.setCompanyName(resume.getJob().getCompany().getName());
        }
        resFetchResumeDto.setUser(new ResFetchResumeDto.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        resFetchResumeDto.setJob(new ResFetchResumeDto.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        return resFetchResumeDto;
    }

    public ResultPaginationDto fetchAllResume(Specification<Resume> specification, Pageable pageable){
        Page<Resume> pageResume = this.resumeRepository.findAll(specification, pageable);
        ResultPaginationDto result = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageResume.getTotalPages());
        meta.setTotal(pageResume.getTotalElements());

        result.setMeta(meta);

        // remove sensitive data
        List<ResFetchResumeDto> listResume = pageResume.getContent()
                .stream().map(item -> this.getResume(item))
                .collect(Collectors.toList());

        result.setResult(listResume);

        return result;
    }

    public ResultPaginationDto fetchResumeByUser(Pageable pageable) {
        // query builder
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDto result = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageResume.getTotalPages());
        meta.setTotal(pageResume.getTotalElements());

        result.setMeta(meta);

        // remove sensitive data
        List<ResFetchResumeDto> listResume = pageResume.getContent()
                .stream().map(item -> this.getResume(item))
                .collect(Collectors.toList());

        result.setResult(listResume);

        return result;
    }

}
