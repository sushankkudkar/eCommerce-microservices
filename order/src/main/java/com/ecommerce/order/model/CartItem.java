package com.ecommerce.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Store only the User ID (string/long) instead of User entity
    @Column(name = "user_id", nullable = false)
    private String userId;

    // Store only the Product ID instead of Product entity
    @Column(name = "product_id", nullable = false)
    private Long productId;

    private Integer quantity;
    private BigDecimal price;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
