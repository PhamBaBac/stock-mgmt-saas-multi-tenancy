package com.bacpham.saas.requests;

import com.bacpham.saas.entities.TypeMvt;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMvtRequest {

    private TypeMvt typeMvt;
    @Positive(message = "Quantity should be a positive number")
    private Integer quantity;
    // @NotNull(message = "Date of movement should not be empty")
    // @PastOrPresent(message = "Date of movement should be in the past or present")
    private LocalDate dateMvt;
    private String comment;
    @NotBlank(message = "Product ID should not be empty")
    private String productId;
}
