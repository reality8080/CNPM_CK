package com.example.spring_boot.domains.products;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.example.spring_boot.domains.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "product_reviews")
public class ProductReview {
    @Id private String id;
    @Field(targetType = FieldType.OBJECT_ID) private ObjectId productId;
    private String name;
    private String email;
    private Integer rating;
    private String comment;
    @Builder.Default private Instant createdAt = Instant.now();
    private Instant deletedAt;
}