package com.example.security.services;

import com.example.security.entity.User;
import com.example.security.repos.UserRepository;
import com.example.security.utils.ApiResponse;
import com.example.security.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll(Sort.by("userId"));
    }

    public User getUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User addNewUser(User user) {
        Optional<User> emailOrUsernameExist = userRepository.findByUsername(user.getUsername());
        if (emailOrUsernameExist.isPresent()) {
            throw new CustomException("UserName is taken", 400);
        }
        return userRepository.save(user);
    }

    public ResponseEntity<ApiResponse> updateUser(User user, long id) {
        User prevUser = userRepository.findById(id).orElse(null);
        if (prevUser == null) {
            ApiResponse apiResponse = new ApiResponse(false, null, "User not found !");
            return ResponseEntity.status(400).body(apiResponse);
        }

        User emailOrUsernameExist = userRepository.findByNotIdAndUsername(id, user.getUsername());
        if (emailOrUsernameExist != null) {
            ApiResponse apiResponse = new ApiResponse(false, null, "Email/Username already exist !");
            return ResponseEntity.status(400).body(apiResponse);
        }

        prevUser.setRoles(user.getRoles());
        prevUser.setUsername(user.getUsername());
        prevUser.setPassword(user.getPassword());
        User newUser = userRepository.save(prevUser);

        ApiResponse apiResponse = new ApiResponse(true, newUser, "User updated successfully !");
        return ResponseEntity.status(200).body(apiResponse);
    }

    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }
}
