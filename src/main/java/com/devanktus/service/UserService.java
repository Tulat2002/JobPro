package com.devanktus.service;

import com.devanktus.entity.User;
import com.devanktus.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user){
        return this.userRepository.save(user);
    }

    public User fetchUserById(long id){
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent())
            return userOptional.get();
        return null;
    }

    public List<User> fetchAllUser(){
        return this.userRepository.findAll();
    }

    public User updateUser(User user){
        User currentUser = this.fetchUserById(user.getId());
        if (currentUser != null){
            currentUser.setName(user.getName());
            currentUser.setEmail(user.getEmail());
            currentUser.setPassword(user.getPassword());
            this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public void deleteUser(long id){
        this.userRepository.deleteById(id);
    }

}
