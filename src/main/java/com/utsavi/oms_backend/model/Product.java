package com.utsavi.oms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_product_id_seq")
    @SequenceGenerator(name = "products_product_id_seq", sequenceName = "products_product_id_seq", allocationSize = 1)
    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(nullable = false)
    private String name;
}
