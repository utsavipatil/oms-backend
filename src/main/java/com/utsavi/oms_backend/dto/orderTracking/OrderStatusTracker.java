package com.utsavi.oms_backend.dto.orderTracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderStatusTracker {
  private Integer orderId;
  private String customerName;
  private String currentStatus;
  private BigDecimal totalAmount;
  List<StatusHistory> statusHistoryList;
}
