package hcmute.edu.vn.web.Entity.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "PromotionProgram")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionProgram {
    @Id
    private String promotionId;

    @NotBlank(message = "Promotion name must not be blank")
    @Size(max = 100, message = "Promotion name must not exceed 100 characters")
    private String promotionName;

    @NotBlank(message = "Promotion type must not be blank")
    @Size(max = 50, message = "Promotion type must not exceed 50 characters")
    private String promotionType;

    @NotNull(message = "Discount value must not be null")
    @PositiveOrZero(message = "Discount value must be positive or zero")
    private Double discountValue;

    @NotNull(message = "Start date must not be null")
    private LocalDateTime startDate;

    @NotNull(message = "End date must not be null")
    private LocalDateTime endDate;

    @Size(max = 200, message = "Apply condition must not exceed 200 characters")
    private String applyCondition;

    @NotBlank(message = "Status must not be blank")
    @Pattern(regexp = "^(Active|Inactive)$", message = "Status must be either Active or Inactive")
    private String status;

    @DBRef
    private List<Product> products;
}