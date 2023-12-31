package com.example.security.services;

import com.example.security.entity.Role;
import com.example.security.repos.RoleRepository;
import com.example.security.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public Role addNewRole(Role role) {

            return roleRepository.save(role);

    }

    public ResponseEntity<ApiResponse> updateRole(Role role, long id) {
        Role prevRole = roleRepository.findById(id).orElse(null);
        if (prevRole == null) {
            ApiResponse apiResponse = new ApiResponse(false, null, "Role not found !");
            return ResponseEntity.status(400).body(apiResponse);
        }

        Role roleExist = roleRepository.findByNotIdAndName(id, role.getName());
        if (roleExist != null) {
            ApiResponse apiResponse = new ApiResponse(false, null, "Role already exist !");
            return ResponseEntity.status(400).body(apiResponse);
        }

        prevRole.setName(role.getName());
        prevRole.setAuthorities(role.getAuthorities());
        Role updatedRole = roleRepository.save(prevRole);

        ApiResponse apiResponse = new ApiResponse(true, updatedRole, "Role updated successfully !");
        return ResponseEntity.status(200).body(apiResponse);
    }
}
