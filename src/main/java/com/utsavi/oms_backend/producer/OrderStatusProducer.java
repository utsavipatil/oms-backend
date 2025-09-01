package com.utsavi.oms_backend.producer;

import com.utsavi.oms_backend.model.OrderStatusHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusProducer {
    @Autowired
    private KafkaTemplate<String, OrderStatusHistory> kafkaTemplate;

    public void publishStatusChange(OrderStatusHistory event) {
        kafkaTemplate.send("order.status", event.getOrderId().toString(), event);
    }
}
