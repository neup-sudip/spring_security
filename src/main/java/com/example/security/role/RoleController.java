package com.example.security.role;

import com.example.security.authority.Authority;
import com.example.security.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('GET_ALL_ROLE')")
    public ResponseEntity<ApiResponse> getAllRoles(){
        ApiResponse apiResponse = new ApiResponse(true, roleService.getRoles(), "All roles fetched", 200);
        return ResponseEntity.status(200).body(apiResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GET_SINGLE_ROLE')")
    public ResponseEntity<ApiResponse> getRoleById(@PathVariable long id) {
        Role role = roleService.getRoleById(id);
        if (role == null) {
            ApiResponse apiResponse = new ApiResponse(true, null, "Error fetching role", 400);
            return ResponseEntity.status(400).body(apiResponse);
        } else {
            ApiResponse apiResponse = new ApiResponse(true, role, "Role fetched successfully", 200);
            return ResponseEntity.status(200).body(apiResponse);
        }
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('GET_SINGLE_ROLE')")
    public ResponseEntity<ApiResponse> getRoleByName(@PathVariable String name) {
        Optional<Role> role = roleService.getRoleByName(name);
        if (role.isPresent()) {
            ApiResponse apiResponse = new ApiResponse(true, role, "Role fetched successfully", 200);
            return ResponseEntity.status(200).body(apiResponse);
        } else {
            ApiResponse apiResponse = new ApiResponse(true, null, "Error fetching role", 400);
            return ResponseEntity.status(400).body(apiResponse);
        }
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    public ResponseEntity<ApiResponse> createRole(@RequestBody NewRoleReq newRole) {
        Authority authority = new Authority();
        authority.setAuthorityId(newRole.getAuthorityId());

        Role role = new Role();
        role.setName(newRole.getName());

        Role resRole = roleService.addNewRole(role);
        ApiResponse apiResponse = new ApiResponse(true, resRole, "Role created successfully !", 200);
        return ResponseEntity.status(200).body(apiResponse);
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('UPDATE_ROLE')")
    public ResponseEntity<ApiResponse> editRole(@RequestBody NewRoleReq newRole, @PathVariable long id) {
        Authority authority = new Authority();
        authority.setAuthorityId(newRole.getAuthorityId());

        Role role = new Role();
        role.setName(newRole.getName());

        return roleService.updateRole(role, id);
    }
}
