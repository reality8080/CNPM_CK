package com.example.spring_boot.services;

import com.example.spring_boot.domains.Role;
import com.example.spring_boot.repository.RoleRepository;
import com.example.spring_boot.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j; // SỬA: Thêm import Lombok Slf4j
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
// XÓA: import java.util.logging.Logger;

@Service
@Slf4j // SỬA: Thêm annotation Slf4j
public class RoleService {
    private final RoleRepository roleRepo;
    // XÓA: private static final Logger logger = Logger.getLogger(RoleService.class.getName());

    public RoleService(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    public Role create(Role input) {
        try {
            if (input == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role data is required");
            }

            String name = ValidationUtils.validateName(input.getName(), 100);
            if (roleRepo.existsByName(name)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Role name already exists");
            }

            Role role = Role.builder()
                    .name(name)
                    .description(ValidationUtils.normalize(input.getDescription()))
                    .createdAt(Instant.now())
                    .build();

            Role savedRole = roleRepo.save(role);
            // SỬA: Dùng log.info
            log.info("Role created successfully: {}", savedRole.getId());
            return savedRole;

        } catch (ResponseStatusException e) {
            // SỬA: Dùng log.warn
            log.warn("Role creation failed: {}", e.getReason());
            throw e;
        } catch (Exception e) {
            // SỬA: Dùng log.error (truyền Exception e)
            log.error("Unexpected error creating role", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create role");
        }
    }

    public List<Role> list(String q) {
        try {
            String keyword = ValidationUtils.normalize(q);
            List<Role> roles = keyword == null ?
                    roleRepo.findAll() :
                    roleRepo.findByNameContainingIgnoreCase(keyword);

            // SỬA: Dùng log.info
            log.info("Retrieved {} roles", roles.size());
            return roles;

        } catch (Exception e) {
            // SỬA: Dùng log.error (truyền Exception e)
            log.error("Error retrieving roles", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve roles");
        }
    }

    public Role get(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role ID is required");
            }

            Role role = roleRepo.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

            // SỬA: Dùng log.info
            log.info("Retrieved role: {}", role.getId());
            return role;

        } catch (ResponseStatusException e) {
            // SỬA: Dùng log.warn
            log.warn("Role retrieval failed: {}", e.getReason());
            throw e;
        } catch (Exception e) {
            // SỬA: Dùng log.error (truyền Exception e)
            log.error("Unexpected error retrieving role", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve role");
        }
    }

    public Role update(String id, Role input) {
        try {
            if (input == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role data is required");
            }

            Role existingRole = get(id);

            String name = ValidationUtils.normalize(input.getName());
            if (name != null && !name.equalsIgnoreCase(existingRole.getName())) {
                ValidationUtils.validateName(name, 100);
                if (roleRepo.existsByName(name)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Role name already exists");
                }
                existingRole.setName(name);
            }

            String description = ValidationUtils.normalize(input.getDescription());
            if (description != null) {
                existingRole.setDescription(description);
            }

            existingRole.setUpdatedAt(Instant.now());
            Role updatedRole = roleRepo.save(existingRole);

            // SỬA: Dùng log.info
            log.info("Role updated successfully: {}", updatedRole.getId());
            return updatedRole;

        } catch (ResponseStatusException e) {
            // SỬA: Dùng log.warn
            log.warn("Role update failed: {}", e.getReason());
            throw e;
        } catch (Exception e) {
            // SỬA: Dùng log.error (truyền Exception e)
            log.error("Unexpected error updating role", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update role");
        }
    }

    public void delete(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role ID is required");
            }

            if (!roleRepo.existsById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
            }

            roleRepo.deleteById(id);
            // SỬA: Dùng log.info
            log.info("Role deleted successfully: {}", id);

        } catch (ResponseStatusException e) {
            // SỬA: Dùng log.warn
            log.warn("Role deletion failed: {}", e.getReason());
            throw e;
        } catch (Exception e) {
            // SỬA: Dùng log.error (truyền Exception e)
            log.error("Unexpected error deleting role", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete role");
        }
    }
}