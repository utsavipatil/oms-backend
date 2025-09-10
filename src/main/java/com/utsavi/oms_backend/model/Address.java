package com.utsavi.oms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addresses_address_id_seq")
    @SequenceGenerator(name = "addresses_address_id_seq", sequenceName = "addresses_address_id_seq", allocationSize = 1)
    @Column(name = "address_id", nullable = false)
    private Integer addressId;

    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String zipcode;
}
