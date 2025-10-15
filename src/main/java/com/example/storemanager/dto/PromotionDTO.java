// PromotionDTO.java
package com.example.storemanager.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class PromotionDTO {
    private String id;
    private String name;
    private String type;
    private BigDecimal value;
    private Instant startDate;
    private Instant endDate;
    private BigDecimal minOrderValue;
    private Integer maxUsage;
    private boolean active;
    private Integer priority;
}
