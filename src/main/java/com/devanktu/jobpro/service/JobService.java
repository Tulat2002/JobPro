package com.devanktu.jobpro.service;

import com.devanktu.jobpro.domain.Company;
import com.devanktu.jobpro.domain.Job;
import com.devanktu.jobpro.domain.Skill;
import com.devanktu.jobpro.domain.dto.response.job.ResCreateJobDto;
import com.devanktu.jobpro.domain.dto.response.job.ResUpdateJobDto;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.repository.CompanyRepository;
import com.devanktu.jobpro.repository.JobRepository;
import com.devanktu.jobpro.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public Optional<Job> fetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResCreateJobDto createJob(Job job){
        // check skills
        if (job.getSkills() != null){
            List<Long> requestSkills = job.getSkills()
                    .stream().map(s -> s.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(requestSkills);
            job.setSkills(dbSkills);

            // check company
            if (job.getCompany() != null) {
                Optional<Company> companyOptional = this.companyRepository.findById(job.getCompany().getId());
                if (companyOptional.isPresent()) {
                    job.setCompany(companyOptional.get());
                }
            }
        }
        //create job
        Job currentJob = this.jobRepository.save(job);
        // convert response 
        ResCreateJobDto resCreateJobDto = new ResCreateJobDto();
        resCreateJobDto.setId(currentJob.getId());
        resCreateJobDto.setName(currentJob.getName());
        resCreateJobDto.setSalary(currentJob.getSalary());
        resCreateJobDto.setQuantity(currentJob.getQuantity());
        resCreateJobDto.setLocation(currentJob.getLocation());
        resCreateJobDto.setLevel(currentJob.getLevel());
        resCreateJobDto.setStartDate(currentJob.getStartDate());
        resCreateJobDto.setEndDate(currentJob.getEndDate());
        resCreateJobDto.setActive(currentJob.isActive());
        resCreateJobDto.setCreatedAt(currentJob.getCreatedAt());
        resCreateJobDto.setCreatedBy(currentJob.getCreatedBy());
        if (currentJob.getSkills() != null){
            List<String> skills = currentJob.getSkills()
                    .stream().map(s -> s.getName()).collect(Collectors.toList());
            resCreateJobDto.setSkills(skills);
        }
        return resCreateJobDto;
    }

    public ResUpdateJobDto updateJob(Job job, Job jobInDB) {
        //check skills
        if (job.getSkills() != null){
            List<Long> requestSkills = job.getSkills()
                    .stream().map(s -> s.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(requestSkills);
            jobInDB.setSkills(dbSkills);
        }

        // check company
        if (job.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(job.getCompany().getId());
            if (cOptional.isPresent()) {
                jobInDB.setCompany(cOptional.get());
            }
        }

        // update correct info
        jobInDB.setName(job.getName());
        jobInDB.setSalary(job.getSalary());
        jobInDB.setQuantity(job.getQuantity());
        jobInDB.setLocation(job.getLocation());
        jobInDB.setLevel(job.getLevel());
        jobInDB.setStartDate(job.getStartDate());
        jobInDB.setEndDate(job.getEndDate());
        jobInDB.setActive(job.isActive());

        //update job
        Job currentJob = this.jobRepository.save(jobInDB);
        //convert response
        ResUpdateJobDto resUpdateJobDto = new ResUpdateJobDto();
        resUpdateJobDto.setId(currentJob.getId());
        resUpdateJobDto.setName(currentJob.getName());
        resUpdateJobDto.setSalary(currentJob.getSalary());
        resUpdateJobDto.setQuantity(currentJob.getQuantity());
        resUpdateJobDto.setLocation(currentJob.getLocation());
        resUpdateJobDto.setLevel(currentJob.getLevel());
        resUpdateJobDto.setStartDate(currentJob.getStartDate());
        resUpdateJobDto.setEndDate(currentJob.getEndDate());
        resUpdateJobDto.setActive(currentJob.isActive());
        resUpdateJobDto.setUpdatedAt(currentJob.getUpdatedAt());
        resUpdateJobDto.setUpdatedBy(currentJob.getUpdatedBy());
        if (currentJob.getSkills() != null){
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName()).collect(Collectors.toList());
            resUpdateJobDto.setSkills(skills);
        }
        return resUpdateJobDto;
    }

    public void deleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDto fetchAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDto result = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());
        result.setMeta(meta);
        result.setResult(pageJob.getContent());
        return result;
    }

}
