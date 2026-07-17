package com.devanktu.jobpro.controller;

import com.devanktu.jobpro.domain.Role;
import com.devanktu.jobpro.domain.dto.response.ResultPaginationDto;
import com.devanktu.jobpro.service.RoleService;
import com.devanktu.jobpro.utils.annotation.ApiMessage;
import com.devanktu.jobpro.utils.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) throws IdInvalidException {
        //check name
        if (this.roleService.existByName(role.getName())) {
            throw new IdInvalidException("Role với name = " + role.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.createRole(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role role) throws IdInvalidException {
        //check id
        if (this.roleService.fetchById(role.getId()) == null) {
            throw new IdInvalidException("Role với id = " + role.getId() + " không tồn tại");
        }
        //check name
//        if (this.roleService.existByName(role.getName())) {
//            throw new IdInvalidException("Role với name = " + role.getName() + " đã tồn tại");
//        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(this.roleService.updateRole(role));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws IdInvalidException {
        //check id
        if (this.roleService.fetchById(id) == null) {
            throw new IdInvalidException("Role với id = " + id + " không tồn tại");
        }
        this.roleService.deleteRoleById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch roles")
    public ResponseEntity<ResultPaginationDto> getPermissions(
            @Filter Specification<Role> spec, Pageable pageable) {

        return ResponseEntity.ok(this.roleService.getAllRoles(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getById(@PathVariable("id") long id) throws IdInvalidException {

        Role role = this.roleService.fetchById(id);
        if (role == null) {
            throw new IdInvalidException("Resume với id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok().body(role);
    }

//    @GetMapping("/roles/{id}")
//    @ApiMessage("Fetch role by id")
//    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id) throws IdInvalidException {
//        Role role = this.roleService.fetchById(id);
//        if (role != null) {
//            return ResponseEntity.ok(role);
//        }else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//    }

}
