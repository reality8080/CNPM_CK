// ProductDTO.java
package com.example.storemanager.dto;
import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String id;
    private String name;
    private String description;
    private Integer quantity;
    private String categoryId;
    private List<String> images;
    private BigDecimal price;
}
