package com.betolara1.product.dto.response;

import java.math.BigDecimal;

public record ProductEvent (Long productId, String sku, String name, BigDecimal price){
    
}
