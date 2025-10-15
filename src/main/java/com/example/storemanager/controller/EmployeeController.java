package com.example.storemanager.controller;

import com.example.storemanager.repository.UserRepository;
import com.example.storemanager.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.storemanager.dto.EmployeeDTO;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager/employees")
public class EmployeeController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        if (username == null) return ResponseEntity.ok(List.of());

        return userRepository.findByUsername(username)
                .map(u -> ResponseEntity.ok(List.of(new EmployeeDTO(u.getId(), u.getUsername(), u.getFullName(), u.getRole()))))
                .orElse(ResponseEntity.ok(List.of()));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmployeeDTO> getMyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        if (username == null) return ResponseEntity.status(401).build();
        return userRepository.findByUsername(username)
                .map(u -> ResponseEntity.ok(new EmployeeDTO(u.getId(), u.getUsername(), u.getFullName(), u.getRole())))
                .orElse(ResponseEntity.status(404).build());
    }

}
