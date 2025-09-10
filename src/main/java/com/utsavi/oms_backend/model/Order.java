package com.utsavi.oms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_order_id_seq")
    @SequenceGenerator(name = "orders_order_id_seq", sequenceName = "orders_order_id_seq", allocationSize = 1)
    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "address_id", nullable = false)
    private Integer addressId;

    @Column(name = "order_date", nullable = false)
    private Timestamp orderDate;

    @Column(nullable = false)
    private String status;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "special_instructions")
    private String specialInstructions;
}
