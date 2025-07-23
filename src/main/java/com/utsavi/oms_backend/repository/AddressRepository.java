package com.utsavi.oms_backend.repository;

import com.utsavi.oms_backend.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    Optional<Address> findByStreetAddressAndCityAndStateAndZipcode(String streetAddress, String city, String state, String zipcode);
}
