package com.example.security.user;

import com.example.security.utils.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

   @GetMapping("/users")
   @PreAuthorize("hasAuthority('GET_ALL_USER')")
   public  ResponseEntity<ApiResponse> getAllUsers(){
        ApiResponse apiResponse = new ApiResponse(true, userService.getUsers(), "All users fetched", 200);
        return ResponseEntity.status(200).body(apiResponse);
   }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('GET_SINGLE_USER')")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            ApiResponse apiResponse = new ApiResponse(true, null, "Error fetching user", 400);
            return ResponseEntity.status(400).body(apiResponse);
        } else {
            ApiResponse apiResponse = new ApiResponse(true, user, "User fetched successfully", 200);
            return ResponseEntity.status(200).body(apiResponse);
        }
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<ApiResponse> createUser(@RequestBody User newUser) {
        User user = userService.addNewUser(newUser);
        ApiResponse apiResponse = new ApiResponse(true, user, "User created successfully !", 200);
        return ResponseEntity.status(200).body(apiResponse);
    }

    @PutMapping("/users/edit/{id}")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public ResponseEntity<ApiResponse> editUser(@RequestBody User newUser, @PathVariable long id) {
            return userService.updateUser(newUser, id);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody User user, HttpServletResponse response) {
        User returnUser = userService.login(user.getUsername(), user.getPassword());
        if (returnUser != null) {
            ApiResponse apiResponse = new ApiResponse(true, null, "user success", 200);
            return ResponseEntity.status(200).body(apiResponse);
        } else {
            ApiResponse apiResponse = new ApiResponse(false, null, "user not found", 400);
            return ResponseEntity.status(400).body(apiResponse);
        }
    }
}
