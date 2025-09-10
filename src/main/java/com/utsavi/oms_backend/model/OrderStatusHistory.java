package com.utsavi.oms_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_status_history")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_status_history_id_seq")
    @SequenceGenerator(name = "order_status_history_id_seq", sequenceName = "order_status_history_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer Id;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "previous_status")
    private String prevStatus;

    @Column(name = "new_Status", nullable = false)
    private String newStatus;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "changed_by", nullable = false)
    private String changeBy;
}
