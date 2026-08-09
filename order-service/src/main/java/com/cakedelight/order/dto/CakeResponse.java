package com.cakedelight.order.dto;

import java.math.BigDecimal;

public record CakeResponse(
        Long id,
        String name,
        BigDecimal price,
        Boolean available
) {
}