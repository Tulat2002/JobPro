package com.devanktu.jobpro.controller;

import com.devanktu.jobpro.domain.Company;
import com.devanktu.jobpro.domain.Job;
import com.devanktu.jobpro.domain.Resume;
import com.devanktu.jobpro.domain.User;
import com.devanktu.jobpro.domain.dto.response.resume.ResCreateResumeDto;
import com.devanktu.jobpro.domain.dto.response.resume.ResFetchResumeDto;
import com.devanktu.jobpro.domain.dto.response.resume.ResUpdateResumeDto;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.service.ResumeService;
import com.devanktu.jobpro.service.UserService;
import com.devanktu.jobpro.utils.SecurityUtil;
import com.devanktu.jobpro.utils.annotation.ApiMessage;
import com.devanktu.jobpro.utils.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;

    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService, UserService userService, FilterBuilder filterBuilder, FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDto> createResume(@Valid @RequestBody Resume resume) throws IdInvalidException {
        //check id exists
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(resume);
        if (!isIdExist)
            throw new IdInvalidException("User id/Job id không tồn tại");
        //create new resume
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.createResume(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDto> updateResume(@RequestBody Resume resume) throws IdInvalidException {
        //check id exists
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(resume.getId());
        if (reqResumeOptional.isEmpty())
            throw new IdInvalidException("Resume với id = " + resume.getId() + " không tồn tại");
        Resume reqResume = reqResumeOptional.get();
        reqResume.setStatus(resume.getStatus());
        return ResponseEntity.ok().body(this.resumeService.updateResume(reqResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.fetchById(id);
        if (resumeOptional.isEmpty())
            throw new IdInvalidException("Resume với id = " + id + " không tồn tại");
        this.resumeService.deleteResume(id);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDto> fetchResumeByUser(Pageable pageable) {

        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch resume by id")
    public ResponseEntity<ResFetchResumeDto> fetchById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.fetchById(id);
        if (resumeOptional.isEmpty())
            throw new IdInvalidException("Resume với id = " + id + " không tồn tại");
        return ResponseEntity.ok().body(this.resumeService.getResume(resumeOptional.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resume with paginate")
    public ResponseEntity<ResultPaginationDto> fetchAll(@Filter Specification<Resume> spec,Pageable pageable) {
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }
        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);

        return ResponseEntity.ok().body(this.resumeService.fetchAllResume(finalSpec, pageable));
    }

}
