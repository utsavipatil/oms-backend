package com.utsavi.oms_backend.dto.orderTracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StatusHistory {
  private String newStatus;
  private String prevStatus;
  private LocalDateTime changedAt;
  private String changedBy;
}
