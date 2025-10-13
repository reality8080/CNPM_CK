package hcmute.edu.vn.web.Entity.Product;

import jakarta.validation.constraints.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "Product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    private String productId;

    @NotBlank(message = "Product name must not be blank")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    private String productName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Price must not be null")
    @PositiveOrZero(message = "Price must be positive or zero")
    private Double price;

    @NotBlank(message = "Image URL must not be blank")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @NotBlank(message = "Status Swami must not be blank")
    @Pattern(regexp = "^(Active|Inactive)$", message = "Status must be either Active or Inactive")
    private String status;

    @NotNull(message = "Sold quantity must not be null")
    @PositiveOrZero(message = "Sold quantity must be positive or zero")
    private Integer soldQuantity;

    @NotNull(message = "Created date must not be null")
    private LocalDateTime createdDate;

    @NotNull(message = "Rating score must not be null")
    @PositiveOrZero(message = "Rating score must be positive or zero")
    private Double ratingScore;

    @NotNull(message = "Favorite count must not be null")
    @PositiveOrZero(message = "Favorite count must be positive or zero")
    private Integer favoriteCount;

    @NotBlank(message = "Category ID must not be blank")
    @Field("category")
    private String categoryId;

    @NotBlank(message = "Supplier ID must not be blank")
    @Field("supplier")
    private String supplierId;
}