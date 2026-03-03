package com.betolara1.Product.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateProductRequest {
    private String sku;
    private String name;
    private String description;

    @Min(value = 0)
    private BigDecimal price;

    @Min(value = 0)
    private Long categoryId;

    private String imageUrl;
    private Boolean active;
}
