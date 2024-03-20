package com.example.security.services;

import com.example.security.entity.UserEntity;
import com.example.security.repos.UserRepository;
import com.example.security.utils.ApiResponse;
import com.example.security.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserEntity> getUsers() {
        return userRepository.findAll(Sort.by("userId"));
    }

    public Optional<UserEntity> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public UserEntity getUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserEntity addNewUser(UserEntity userEntity) {
        Optional<UserEntity> emailOrUsernameExist = userRepository.findByUsername(userEntity.getUsername());
        if (emailOrUsernameExist.isPresent()) {
            throw new CustomException("UserName is taken", 400);
        }
        return userRepository.save(userEntity);
    }

    public ResponseEntity<ApiResponse> updateUser(UserEntity userEntity, long id) {
        UserEntity prevUserEntity = userRepository.findById(id).orElse(null);
        if (prevUserEntity == null) {
            ApiResponse apiResponse = new ApiResponse(false, null, "User not found !");
            return ResponseEntity.status(400).body(apiResponse);
        }

        UserEntity emailOrUsernameExist = userRepository.findByNotIdAndUsername(id, userEntity.getUsername());
        if (emailOrUsernameExist != null) {
            ApiResponse apiResponse = new ApiResponse(false, null, "Email/Username already exist !");
            return ResponseEntity.status(400).body(apiResponse);
        }

        prevUserEntity.setRoles(userEntity.getRoles());
        prevUserEntity.setUsername(userEntity.getUsername());
        prevUserEntity.setPassword(userEntity.getPassword());
        UserEntity newUserEntity = userRepository.save(prevUserEntity);

        ApiResponse apiResponse = new ApiResponse(true, newUserEntity, "User updated successfully !");
        return ResponseEntity.status(200).body(apiResponse);
    }

    public UserEntity login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }
}
