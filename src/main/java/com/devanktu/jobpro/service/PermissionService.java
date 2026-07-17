package com.devanktu.jobpro.service;

import com.devanktu.jobpro.domain.Permission;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.repository.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(), permission.getApiPath(), permission.getMethod()
        );
    }

    public Permission fetchById(long id){
        Optional<Permission> optionalPermission = this.permissionRepository.findById(id);
        if (optionalPermission.isPresent()) {
            return optionalPermission.get();
        }
        return null;
    }

    public Permission createPermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission updatePermission(Permission permission) {
        Permission permissionDb = this.fetchById(permission.getId());
        if (permissionDb != null){
            permissionDb.setName(permission.getName());
            permissionDb.setMethod(permission.getMethod());
            permissionDb.setApiPath(permission.getApiPath());
            permissionDb.setModule(permission.getModule());
            //update
            permissionDb = this.permissionRepository.save(permissionDb);
            return permissionDb;
        }
        return null;
    }

    public void deletePermission(long id) {
        // delete permission_role
        Optional<Permission> optionalPermission = this.permissionRepository.findById(id);
        Permission currentPermission = optionalPermission.get();
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));
        // delete permission
        this.permissionRepository.delete(currentPermission);
    }

    public boolean isSameName(Permission permission) {
        Permission permissionDB = this.fetchById(permission.getId());
        if (permissionDB != null) {
            if (permissionDB.getName().equals(permission.getName()))
                return true;
        }
        return false;
    }

    public ResultPaginationDto getPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermissions = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDto rs = new ResultPaginationDto();
        ResultPaginationDto.Meta mt = new ResultPaginationDto.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pagePermissions.getTotalPages());
        mt.setTotal(pagePermissions.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pagePermissions.getContent());
        return rs;
    }

}
