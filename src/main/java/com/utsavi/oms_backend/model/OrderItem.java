package com.utsavi.oms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Builder
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_items_order_item_id_seq")
    @SequenceGenerator(name = "order_items_order_item_id_seq", sequenceName = "order_items_order_item_id_seq", allocationSize = 1)
    @Column(name = "order_item_id", nullable = false)
    private Integer orderItemId;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(nullable = false)
    private Integer quantity;

    private BigDecimal price;
}
