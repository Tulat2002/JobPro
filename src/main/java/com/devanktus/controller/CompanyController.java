package com.devanktus.controller;

import com.devanktus.annotation.ApiMessage;
import com.devanktus.dto.response.ResultPaginationDTO;
import com.devanktus.entity.Company;
import com.devanktus.service.CompanyService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    @ApiMessage("Create company")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.createCompany(company));
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch all company")
    public ResponseEntity<ResultPaginationDTO> fetchAllCompanies(@Filter Specification<Company> specification, Pageable pageable){
        return ResponseEntity.ok(this.companyService.fetchAllCompany(specification, pageable));
    }

    @PutMapping("/companies")
    @ApiMessage("Update company")
    public ResponseEntity<Company> updatedCompany(@Valid @RequestBody Company company){
        Company updatedCompany = this.companyService.updateCompany(company);
        return ResponseEntity.ok(updatedCompany);
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("Delete company by id")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id){
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok(null);
    }

}
