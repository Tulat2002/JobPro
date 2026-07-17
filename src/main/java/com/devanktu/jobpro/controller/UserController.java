package com.devanktu.jobpro.controller;

import com.devanktu.jobpro.domain.User;
import com.devanktu.jobpro.domain.dto.response.user.ResCreateUserDto;
import com.devanktu.jobpro.domain.dto.response.user.ResUpdateUserDto;
import com.devanktu.jobpro.domain.dto.response.user.ResUserDto;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.utils.exception.IdInvalidException;
import com.devanktu.jobpro.service.UserService;
import com.devanktu.jobpro.utils.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<ResCreateUserDto> createUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + user.getEmail() + "đã tồn tại, vui lòng sử dụng email khác.");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDto(newUser));
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<ResUserDto> fetchUserById(@PathVariable("id") long id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null)
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDto(fetchUser));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDto> getAllUer(@Filter Specification<User> spec, Pageable pageable){
        return ResponseEntity.ok(this.userService.fetchAllUser(spec, pageable));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null)
            throw new IdInvalidException("User với id = " + id + " không tồn tại");

        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDto> updateUser(@RequestBody User user) throws IdInvalidException {
        User currentUser = this.userService.handleUpdateUser(user);
        if (currentUser == null)
            throw new IdInvalidException("User với id = " + user.getId() + " không tồn tại");
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDto(currentUser));
    }

}
