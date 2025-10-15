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
@Document(collection = "promotions")
public class Promotion {
    @Id
    private String id;
    private String name;
    private String type; // PERCENT | FIXED | SPECIAL_PRICE
    private BigDecimal value;
    private Instant startDate;
    private Instant endDate;
    private BigDecimal minOrderValue;
    private Integer maxUsage;
    private boolean active;
    private Integer priority;
}
