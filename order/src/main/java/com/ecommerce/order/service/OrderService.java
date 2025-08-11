package com.ecommerce.order.service;

import com.ecommerce.order.clients.product.ProductServiceClient;
import com.ecommerce.order.clients.user.UserServiceClient;
import com.ecommerce.order.dto.*;
import com.ecommerce.order.model.*;
// import com.ecommerce.order.repository.UserRepository; // ❌ Commented out User repository
import com.ecommerce.order.repository.CartItemRepository;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    private final UserServiceClient userServiceClient;

    public ResponseEntity<ApiResponse<OrderCreateResponseDto>> createOrder(OrderCreateDto request) {
        try {

            Optional<UserResponseDto> user = userServiceClient.getUser(request.getUserId());
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "User not found", null));
            }

            // Fetch cart items by String userId (no type conversion needed now)
            List<CartItem> cartItems = cartItemRepository.findByUserId(request.getUserId());
            if (cartItems.isEmpty()) {
                return buildResponse(false, "Cart is empty", null, HttpStatus.BAD_REQUEST);
            }

            // Create new order
            Order order = new Order();
            order.setUserId(request.getUserId());
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            order.setStatus(OrderStatus.CONFIRMED);

            List<OrderItem> orderItems = new ArrayList<>();
            List<OrderItemDto> orderItemDtos = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            // Convert cart items to order items
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProductId(cartItem.getProductId()); // ✅ Store productId directly
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());

                orderItems.add(orderItem);
                totalAmount =totalAmount.add(
                        Optional.ofNullable(cartItem.getPrice()).orElse(BigDecimal.ZERO)
                );

                orderItemDtos.add(OrderItemDto.builder()
                        .productId((cartItem.getProductId()))
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getPrice())
                        .build());
            }

            order.setItems(orderItems);
            order.setTotalAmount(totalAmount);

            // Save order
            Order savedOrder = orderRepository.save(order);

            // Clear cart after placing order
            cartItemRepository.deleteAll(cartItems);


            // Publish Order Created Event

            List<OrderItemDto> itemDtos = savedOrder.getItems().stream()
                    .map(item -> OrderItemDto.builder()
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .build()
                    )
                    .toList();

            OrderCreatedEventDto event = OrderCreatedEventDto.builder()
                    .orderId(savedOrder.getId())
                    .userId(savedOrder.getUserId())
                    .status(savedOrder.getStatus())
                    .totalAmount(savedOrder.getTotalAmount())
                    .items(itemDtos)
                    .createdAt(savedOrder.getCreatedAt())
                    .build();

            rabbitTemplate.convertAndSend(exchangeName, routingKey, event);

            // Prepare response DTO
            OrderCreateResponseDto responseDto = OrderCreateResponseDto.builder()
                    .orderId(savedOrder.getId())
                    .totalAmount(savedOrder.getTotalAmount())
                    .status(savedOrder.getStatus().name())
                    .createdAt(savedOrder.getCreatedAt())
                    .items(orderItemDtos)
                    .build();

            return buildResponse(true, "Order placed successfully", responseDto, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return buildResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildResponse(false, "Internal server error", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Utility method to build consistent responses
    private ResponseEntity<ApiResponse<OrderCreateResponseDto>> buildResponse(
            boolean success, String message, OrderCreateResponseDto data, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(success, message, data));
    }
}
