package com.betolara1.Product.dto.response;

import java.math.BigDecimal;

import com.betolara1.Product.model.Product;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String imageUrl;
    private boolean active;

    public ProductDTO() {}

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.sku = product.getSku();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.categoryId = product.getCategoryId();
        this.imageUrl = product.getImageUrl();
        this.active = product.isActive();
    }
}
