package com.devanktus.controller;

import com.devanktus.entity.User;
import com.devanktus.exception.IdInvalidException;
import com.devanktus.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user){
        User newUser = this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> fetchUserById(@PathVariable("id") long id){
        User user = this.userService.fetchUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> fetchAllUser(){
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser());
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(User user){
        User userUpdate = this.userService.updateUser(user);
        return ResponseEntity.ok(userUpdate);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        if (id >= 1500){
            throw new IdInvalidException("Id khong lon ho 1500");
        }
        this.userService.deleteUser(id);
        return ResponseEntity.ok("Delete Success");
    }

}
