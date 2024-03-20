package com.example.security.controllers;

import com.example.security.entity.UserEntity;
import com.example.security.services.UserService;
import com.example.security.utils.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('GET_ALL_USER')")
    public ResponseEntity<ApiResponse> getAllUsers() {
        ApiResponse apiResponse = new ApiResponse(true, userService.getUsers(), "All users fetched");
        return ResponseEntity.status(200).body(apiResponse);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('GET_SINGLE_USER')")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable long id) {
        UserEntity userEntity = userService.getUserById(id);
        if (userEntity == null) {
            ApiResponse apiResponse = new ApiResponse(true, null, "Error fetching user");
            return ResponseEntity.status(400).body(apiResponse);
        } else {
            ApiResponse apiResponse = new ApiResponse(true, userEntity, "User fetched successfully");
            return ResponseEntity.status(200).body(apiResponse);
        }
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserEntity newUserEntity) {
        UserEntity userEntity = userService.addNewUser(newUserEntity);
        ApiResponse apiResponse = new ApiResponse(true, userEntity, "User created successfully !");
        return ResponseEntity.status(200).body(apiResponse);
    }

    @PutMapping("/users/edit/{id}")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public ResponseEntity<ApiResponse> editUser(@RequestBody UserEntity newUserEntity, @PathVariable long id) {
        return userService.updateUser(newUserEntity, id);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody UserEntity userEntity, HttpServletResponse response) {
        UserEntity returnUserEntity = userService.login(userEntity.getUsername(), userEntity.getPassword());
        if (returnUserEntity != null) {
            ApiResponse apiResponse = new ApiResponse(true, null, "user success");
            return ResponseEntity.status(200).body(apiResponse);
        } else {
            ApiResponse apiResponse = new ApiResponse(false, null, "user not found");
            return ResponseEntity.status(400).body(apiResponse);
        }
    }
}
