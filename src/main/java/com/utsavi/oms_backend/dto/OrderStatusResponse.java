package com.utsavi.oms_backend.dto;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public class OrderStatusResponse {
    private String orderId;
    private String oldStatus;
    private String newStatus;
    private Timestamp changedAt;
    private String changedBy;
    private String message;
}
