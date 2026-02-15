package com.smf.controller;

import com.smf.dto.api.ApiResponse;
import com.smf.dto.role.RoleRequest;
import com.smf.dto.role.RoleResponse;
import com.smf.service.role.IRoleService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/roles")
public class RoleController {

    private final IRoleService roleService;

    public RoleController(IRoleService roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<ApiResponse> createRole(@Valid @RequestBody RoleRequest request) {
        RoleResponse response = roleService.createRole(request);
        return ResponseEntity.ok(new ApiResponse(true, "Role created successfully", response));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getRole(@PathVariable Long id) {
        RoleResponse response = roleService.getRoleById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Role fetched successfully", response));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<ApiResponse> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(new ApiResponse(true, "Roles fetched successfully", roles));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {

        RoleResponse response = roleService.updateRole(id, request);
        return ResponseEntity.ok(new ApiResponse(true, "Role updated successfully", response));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(new ApiResponse(true, "Role deleted successfully", null));
    }
}
