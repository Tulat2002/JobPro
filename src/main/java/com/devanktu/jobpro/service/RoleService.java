package com.devanktu.jobpro.service;

import com.devanktu.jobpro.domain.Permission;
import com.devanktu.jobpro.domain.Role;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.repository.PermissionRepository;
import com.devanktu.jobpro.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existByName(String name){
        return this.roleRepository.existsByName(name);
    }

    public Role createRole(Role role) {
        // check permissions
        if (role.getPermissions() != null) {
            List<Long> reqPermission = role.getPermissions().
                    stream().map(p -> p.getId()).collect(Collectors.toList());

            List<Permission> permissionsList = this.permissionRepository.findByIdIn(reqPermission);
            role.setPermissions(permissionsList);
        }
        return this.roleRepository.save(role);
    }

    public Role fetchById(long id){
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent())
            return roleOptional.get();
        return null;
    }

    public Role updateRole(Role role){
        Role currentRole = this.fetchById(role.getId());
        // check permissions
        if (role.getPermissions() != null){
            List<Long> reqPermission = role.getPermissions()
                    .stream().map(p -> p.getId()).collect(Collectors.toList());
            List<Permission> permissionList = this.permissionRepository.findByIdIn(reqPermission);
            role.setPermissions(permissionList);
        }
        currentRole.setName(role.getName());
        currentRole.setDescription(role.getDescription());
        currentRole.setActive(role.isActive());
        currentRole.setPermissions(role.getPermissions());
        currentRole = this.roleRepository.save(currentRole);
        return currentRole;
    }

    public void deleteRoleById(long id){
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDto getAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDto rs = new ResultPaginationDto();
        ResultPaginationDto.Meta mt = new ResultPaginationDto.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageRole.getTotalPages());
        mt.setTotal(pageRole.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageRole.getContent());
        return rs;
    }

}
