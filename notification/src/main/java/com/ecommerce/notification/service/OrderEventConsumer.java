package com.ecommerce.notification.service;

import com.ecommerce.notification.payload.OrderCreatedEventDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

    @RabbitListener(queues = "order.queue")
    public void handleOrderEvents(OrderCreatedEventDto event) {
        System.out.println("Received Order Event:");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("User ID: " + event.getUserId());
        System.out.println("Status: " + event.getStatus());
        System.out.println("Total Amount: " + event.getTotalAmount());
        System.out.println("Created At: " + event.getCreatedAt());

        if (event.getItems() != null) {
            event.getItems().forEach(item ->
                    System.out.println("   Product " + item.getProductId() +
                            " Qty: " + item.getQuantity() +
                            " Price: " + item.getPrice())
            );
        }
    }
}
