package com.devanktu.jobpro.service;

import com.devanktu.jobpro.domain.Company;
import com.devanktu.jobpro.domain.User;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.repository.CompanyRepository;
import com.devanktu.jobpro.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDto handleGetAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> companyPage = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDto result = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();

        meta.setPage(pageable.getPageNumber() +1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(companyPage.getTotalPages());
        meta.setTotal(companyPage.getTotalElements());

        result.setMeta(meta);
        result.setResult(companyPage.getContent());
        return result;
    }

    public Company handleUpdateCompany(Company company) {
        Optional<Company> companyOptional = this.companyRepository.findById(company.getId());
        if (companyOptional.isPresent()) {
            Company currentCompany = companyOptional.get();
            currentCompany.setName(company.getName());
            currentCompany.setAddress(company.getAddress());
            currentCompany.setLogo(company.getLogo());
            currentCompany.setDescription(company.getDescription());
            this.companyRepository.save(currentCompany);
        }
        return null;
    }

    public void deleteCompany(long id){
        Optional<Company> optionalCompany = this.companyRepository.findById(id);
        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            // fetch all user belong to this company
            List<User> userList = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(userList);
        }
        this.companyRepository.deleteById(id);
    }

    public Optional<Company> findById(long id){
        return this.companyRepository.findById(id);
    }

}
