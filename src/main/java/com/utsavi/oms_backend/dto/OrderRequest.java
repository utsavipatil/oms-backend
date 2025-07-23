package com.utsavi.oms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private String name;
    private String email;
    private String phoneNo;
    private List<ProductRequest> products;
    private String streetAddress;
    private String city;
    private String state;
    private String zipcode;
    private String specialInstructions;
}
