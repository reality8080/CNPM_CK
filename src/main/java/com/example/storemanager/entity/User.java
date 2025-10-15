package com.example.storemanager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String password; // hashed in real app (store plain for demo)
    private String role; // MANAGER, ADMIN, STAFF
    private String fullName;
}
