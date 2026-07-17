package com.devanktu.jobpro.service;

import com.devanktu.jobpro.domain.Company;
import com.devanktu.jobpro.domain.Role;
import com.devanktu.jobpro.domain.User;
import com.devanktu.jobpro.domain.dto.response.*;
import com.devanktu.jobpro.domain.dto.response.user.ResCreateUserDto;
import com.devanktu.jobpro.domain.dto.response.user.ResUpdateUserDto;
import com.devanktu.jobpro.domain.dto.response.user.ResUserDto;
import com.devanktu.jobpro.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) {
        //check company
        if (user.getCompany() != null){
            Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }
        // check role
        if (user.getRole() != null) {
            Role role = this.roleService.fetchById(user.getRole().getId());
            user.setRole(role != null ? role : null);
        }
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id){
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResultPaginationDto fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDto result = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();

        meta.setPage(pageable.getPageNumber() +1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        result.setMeta(meta);
        // remove sensitive data
        List<ResUserDto> listUser = pageUser.getContent()
                .stream().map(item -> this.convertToResUserDto(item))
                .collect(Collectors.toList());

        result.setResult(listUser);
        return result;
    }

    public User handleUpdateUser(User user){
        User currentUser = this.fetchUserById(user.getId());
        if (currentUser != null) {
            currentUser.setName(user.getName());
            currentUser.setAddress(user.getAddress());
            currentUser.setAge(user.getAge());
            currentUser.setGender(user.getGender());
            // check company
            if (user.getCompany() != null) {
                Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
                currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
                // check role
                if (user.getRole() != null) {
                    Role role = this.roleService.fetchById(user.getRole().getId());
                    currentUser.setRole(role != null ? role : null);
                }
            }
            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User handleGetUserByUsername(String username){
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExist(String email){
        return this.userRepository.existsByEmail(email);
    }

    public void updateUserToken(String token, String email){
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null){
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email){
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

    public ResCreateUserDto convertToResCreateUserDto(User user){
        ResCreateUserDto resCreateUserDto = new ResCreateUserDto();
        ResCreateUserDto.CompanyUser companyUserDto = new ResCreateUserDto.CompanyUser();
        resCreateUserDto.setId(user.getId());
        resCreateUserDto.setEmail(user.getEmail());
        resCreateUserDto.setName(user.getName());
        resCreateUserDto.setAddress(user.getAddress());
        resCreateUserDto.setAge(user.getAge());
        resCreateUserDto.setGender(user.getGender());
        resCreateUserDto.setCreatedAt(user.getCreatedAt());
        if (user.getCompany() != null){
            companyUserDto.setId(user.getCompany().getId());
            companyUserDto.setName(user.getCompany().getName());
            resCreateUserDto.setCompany(companyUserDto);
        }
        return resCreateUserDto;
    }

    public ResUpdateUserDto convertToResUpdateUserDto(User user){
        ResUpdateUserDto resUpdateUserDto = new ResUpdateUserDto();
        ResUpdateUserDto.CompanyUser com = new ResUpdateUserDto.CompanyUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            resUpdateUserDto.setCompany(com);
        }
        resUpdateUserDto.setId(user.getId());
        resUpdateUserDto.setName(user.getName());
        resUpdateUserDto.setAge(user.getAge());
        resUpdateUserDto.setUpdatedAt(user.getUpdatedAt());
        resUpdateUserDto.setGender(user.getGender());
        resUpdateUserDto.setAddress(user.getAddress());
        return resUpdateUserDto;
    }

    public ResUserDto convertToResUserDto(User user){
        ResUserDto resUserDto = new ResUserDto();
        ResUserDto.CompanyUser companyUserDto = new ResUserDto.CompanyUser();
        ResUserDto.RoleUser roleUser = new ResUserDto.RoleUser();
        if (user.getCompany() != null){
            companyUserDto.setId(user.getCompany().getId());
            companyUserDto.setName(user.getCompany().getName());
            resUserDto.setCompany(companyUserDto);
        }
        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            resUserDto.setRole(roleUser);
        }
        resUserDto.setId(user.getId());
        resUserDto.setEmail(user.getEmail());
        resUserDto.setName(user.getName());
        resUserDto.setAge(user.getAge());
        resUserDto.setUpdatedAt(user.getUpdatedAt());
        resUserDto.setCreatedAt(user.getCreatedAt());
        resUserDto.setGender(user.getGender());
        resUserDto.setAddress(user.getAddress());
        return resUserDto;
    }

}
