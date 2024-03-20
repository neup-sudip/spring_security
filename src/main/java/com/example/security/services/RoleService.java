package com.example.security.services;

import com.example.security.entity.Role;
import com.example.security.repos.RoleRepository;
import com.example.security.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(long id) {
        return roleRepository.findById(id);
    }

    public Role addNewRole(Role role) {
        return roleRepository.save(role);
    }

    public ResponseEntity<ApiResponse> updateRole(Role role, long id) {
        Optional<Role> optPrevRole = roleRepository.findById(id);
        if (optPrevRole.isEmpty()) {
            ApiResponse apiResponse = new ApiResponse(false, null, "Role not found !");
            return ResponseEntity.status(400).body(apiResponse);
        }

        Role roleExist = roleRepository.findByNotIdAndName(id, role.getName());
        if (roleExist != null) {
            ApiResponse apiResponse = new ApiResponse(false, null, "Role already exist !");
            return ResponseEntity.status(400).body(apiResponse);
        }

        Role prevRole = optPrevRole.get();
        prevRole.setName(role.getName());
        prevRole.setAuthorities(role.getAuthorities());
        Role updatedRole = roleRepository.save(prevRole);

        ApiResponse apiResponse = new ApiResponse(true, updatedRole, "Role updated successfully !");
        return ResponseEntity.status(200).body(apiResponse);
    }
}
