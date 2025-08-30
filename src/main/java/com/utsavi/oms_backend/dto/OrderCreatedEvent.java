package com.utsavi.oms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderCreatedEvent {
    private Integer orderId;
    private String name;
    private String email;
    private String phoneNo;
    private List<ProductRequest> products;
    private String streetAddress;
    private String city;
    private String state;
    private String zipcode;
    private String specialInstructions;
    private String status;
}
