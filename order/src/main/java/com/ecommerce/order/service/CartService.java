package com.ecommerce.order.service;

import com.ecommerce.order.dto.*;
import com.ecommerce.order.model.*;
import com.ecommerce.order.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;

    public ResponseEntity<ApiResponse<Map<String, Object>>> addToCart(AddToCartRequestDto requestDto) {
        try {
            Optional<CartItem> existingCartItemOpt = cartItemRepository
                    .findByUserIdAndProductId(requestDto.getUserId(), requestDto.getProductId());

            CartItem cartItem;
            if (existingCartItemOpt.isPresent()) {
                cartItem = existingCartItemOpt.get();
                int newQuantity = cartItem.getQuantity() + requestDto.getQuantity();
                cartItem.setQuantity(newQuantity);
            } else {
                cartItem = new CartItem();
                cartItem.setUserId(requestDto.getUserId());
                cartItem.setProductId(requestDto.getProductId());
                cartItem.setQuantity(requestDto.getQuantity());

                // TODO: Replace with call to Product Microservice to fetch actual price
                // Example (future):
                // BigDecimal productPrice = productServiceClient.getProductPrice(requestDto.getProductId());
                BigDecimal productPrice = BigDecimal.valueOf(500); // temporary default

                cartItem.setPrice(productPrice);
            }

            CartItem savedItem = cartItemRepository.save(cartItem);

            CartItemResponseDto responseDto = CartItemResponseDto.builder()
                    .id(savedItem.getId())
                    .userId(Long.valueOf(savedItem.getUserId()))
                    .productId(Long.valueOf(savedItem.getProductId()))
                    .quantity(savedItem.getQuantity())
                    .price(savedItem.getPrice())
                    .build();

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("cartItemDetails", responseDto);

            return ResponseEntity.ok(new ApiResponse<>(true, "Cart item added successfully", responseMap));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Internal server error", null));
        }
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> removeFromCart(Long userId, Long productId) {
        try {
            // In future: call User MS to validate userId and Product MS to validate productId before proceeding
            // For now: directly query DB

            Optional<CartItem> existingCartItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);

            if (existingCartItemOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Cart item not found", null));
            }

            CartItem cartItem = existingCartItemOpt.get();
            cartItemRepository.delete(cartItem);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("removedCartItem", CartItemResponseDto.builder()
                    .id(cartItem.getId())
                    .userId(cartItem.getUserId())
                    .productId(cartItem.getProductId())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build());

            return ResponseEntity.ok(new ApiResponse<>(true, "Cart item removed successfully", responseMap));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Internal server error", null));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCartByUserId(Long userId) {
        try {
            List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

            List<CartItemResponseDto> cartItemDtos = cartItems.stream()
                    .map(item -> {
                        // TODO: Replace with call to Product Microservice to enrich with product details
                        // TODO: Replace with call to User Microservice if needed
                        return CartItemResponseDto.builder()
                                .id(item.getId())
                                .userId(item.getUserId())
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build();
                    })
                    .toList();

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("cartItems", cartItemDtos);

            return ResponseEntity.ok(new ApiResponse<>(true, "Cart items fetched successfully", responseMap));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Internal server error", null));
        }
    }
}
