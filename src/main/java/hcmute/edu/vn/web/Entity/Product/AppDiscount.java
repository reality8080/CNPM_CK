package hcmute.edu.vn.web.Entity.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Document(collection = "AppDiscount")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppDiscount {
    @Id
    private String discountId;

    @NotBlank(message = "Shop ID must not be blank")
    private String shopId;

    @NotNull(message = "Discount percentage must not be null")
    @PositiveOrZero(message = "Discount percentage must be positive or zero")
    @Max(value = 100, message = "Discount percentage must not exceed 100")
    private Double discountPercentage;

    @NotNull(message = "Start date must not be null")
    private LocalDateTime startDate;

    @NotNull(message = "End date must not be null")
    private LocalDateTime endDate;

    @NotBlank(message = "Status must not be blank")
    @Pattern(regexp = "^(Active|Inactive)$", message = "Status must be either Active or Inactive")
    private String status;
}