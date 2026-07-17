package com.devanktu.jobpro.controller;

import com.devanktu.jobpro.domain.Company;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.service.CompanyService;
import com.devanktu.jobpro.utils.annotation.ApiMessage;
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
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    @ApiMessage("Create a companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        Company newCompany = this.companyService.handleCreateCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch companies")
    public ResponseEntity<ResultPaginationDto> getCompany(@Filter Specification<Company> spec, Pageable pageable) {
        return ResponseEntity.ok(this.companyService.handleGetAllCompanies(spec, pageable));
    }

    @PutMapping("/companies")
    @ApiMessage("Update a companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) {
        Company updatedCompany = this.companyService.handleUpdateCompany(company);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updatedCompany);
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("Delete a companies")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") Long id) {
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("Fetch company by id")
    public ResponseEntity<Company> fetchCompanyById(@PathVariable("id") long id) {
        Optional<Company> cOptional = this.companyService.findById(id);
        return ResponseEntity.ok().body(cOptional.get());
    }

}
