package com.utsavi.oms_backend.dto.orderTable;

import com.utsavi.oms_backend.model.Address;
import com.utsavi.oms_backend.model.Order;
import com.utsavi.oms_backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderItemDto {
    Order order;
    User user;
    Address address;
    List<Item> itemList;
}
