package com.utsavi.oms_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    private String phoneNo;

    @NotEmpty(message = "At least one product is required")
    private List<ProductRequest> products;

    private String streetAddress;
    private String city;
    private String state;

    @NotBlank(message = "Zipcode is required")
    private String zipcode;
    
    private BigDecimal totalAmount;

    private String specialInstructions;
}
