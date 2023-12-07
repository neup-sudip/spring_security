package com.example.security.role;

import com.example.security.utils.ApiResponse;
import com.example.security.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public Role addNewRole(Role role) {
        Role exist = roleRepository.findByNameAndAuthority(role.getName(), role.getAuthority().getAuthorityId());
        if (exist == null) {
            return roleRepository.save(role);
        } else {
            throw new CustomException("Role already exist", 400);
        }
    }

    public ResponseEntity<ApiResponse> updateRole(Role role, long id) {
        Role prevRole = roleRepository.findById(id).orElse(null);
        if (prevRole == null) {
            ApiResponse apiResponse = new ApiResponse(false, null, "Role not found !", 400);
            return ResponseEntity.status(400).body(apiResponse);
        }

        Role roleExist = roleRepository.findByNotIdAndName(id, role.getName());
        if (roleExist != null) {
            ApiResponse apiResponse = new ApiResponse(false, null, "Role already exist !", 400);
            return ResponseEntity.status(400).body(apiResponse);
        }

        prevRole.setName(role.getName());
        prevRole.setAuthority(role.getAuthority());
        Role updatedRole = roleRepository.save(prevRole);

        ApiResponse apiResponse = new ApiResponse(true, updatedRole, "Role updated successfully !", 200);
        return ResponseEntity.status(200).body(apiResponse);
    }
}
