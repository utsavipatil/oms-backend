package com.utsavi.oms_backend.dto;

import com.utsavi.oms_backend.util.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderStatusRequest {
    @NotNull(message = "New status is required")
    private OrderStatus newStatus;

    private String changedBy;
}
