package com.example.storemanager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Document(collection = "invoice_items")
public class InvoiceItem {
    @Id
    private String id;
    private String invoiceId;
    private String productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
}
