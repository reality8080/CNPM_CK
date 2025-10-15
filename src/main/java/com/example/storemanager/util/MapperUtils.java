package com.example.storemanager.util;

import com.example.storemanager.dto.*;
import com.example.storemanager.entity.*;
import org.springframework.stereotype.Component;

@Component
public class MapperUtils {

    public CategoryDTO toCategoryDTO(Category c) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setDescription(c.getDescription());
        return dto;
    }

    public Category toCategoryEntity(CategoryDTO dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public ProductDTO toProductDTO(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setQuantity(p.getQuantity());
        dto.setCategoryId(p.getCategoryId());
        dto.setImages(p.getImages());
        return dto;
    }

    public Product toProductEntity(ProductDTO dto) {
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .quantity(dto.getQuantity())
                .categoryId(dto.getCategoryId())
                .images(dto.getImages())
                .build();
    }

    public PromotionDTO toPromotionDTO(Promotion p) {
        PromotionDTO dto = new PromotionDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setType(p.getType());
        dto.setValue(p.getValue());
        dto.setStartDate(p.getStartDate());
        dto.setEndDate(p.getEndDate());
        dto.setMinOrderValue(p.getMinOrderValue());
        dto.setMaxUsage(p.getMaxUsage());
        dto.setActive(p.isActive());
        dto.setPriority(p.getPriority());
        return dto;
    }

    public Promotion toPromotionEntity(PromotionDTO dto) {
        return Promotion.builder()
                .id(dto.getId())
                .name(dto.getName())
                .type(dto.getType())
                .value(dto.getValue())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .minOrderValue(dto.getMinOrderValue())
                .maxUsage(dto.getMaxUsage())
                .active(dto.isActive())
                .priority(dto.getPriority())
                .build();
    }
}
