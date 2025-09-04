package com.utsavi.oms_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String name;
    private Integer quantity;
    private BigDecimal price;
}
