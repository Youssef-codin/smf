package com.smf.service.role;

import com.smf.dto.role.RoleRequest;
import com.smf.dto.role.RoleResponse;

import java.util.List;

public interface IRoleService {

    RoleResponse createRole(RoleRequest request);

    RoleResponse getRoleById(Long id);

    List<RoleResponse> getAllRoles();

    RoleResponse updateRole(Long id, RoleRequest request);

    void deleteRole(Long id);
}
