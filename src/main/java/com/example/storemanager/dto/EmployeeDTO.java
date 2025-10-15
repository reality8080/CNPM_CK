package com.example.storemanager.dto;

public class EmployeeDTO {

    private String id;
    private String username;
    private String fullName;
    private String role;

    public EmployeeDTO(String id, String username, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}
