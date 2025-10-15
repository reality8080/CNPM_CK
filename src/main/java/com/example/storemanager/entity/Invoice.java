package com.example.storemanager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Document(collection = "invoices")
public class Invoice {
    @Id
    private String id;
    private Instant createdAt;
    private BigDecimal totalAmount;
    private String status; // PAID, PENDING
}
