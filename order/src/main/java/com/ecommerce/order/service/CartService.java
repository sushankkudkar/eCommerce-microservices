package com.ecommerce.order.service;

import com.ecommerce.order.clients.product.ProductServiceClient;
import com.ecommerce.order.clients.user.UserServiceClient;
import com.ecommerce.order.dto.*;
import com.ecommerce.order.model.*;
import com.ecommerce.order.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    public ResponseEntity<ApiResponse<Map<String, Object>>> addToCart(AddToCartRequestDto requestDto) {
        try {
            Optional<UserResponseDto> userOpt = userServiceClient.getUser(requestDto.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "User not found", null));
            }

            Optional<ProductCreateResponseDto> product = productServiceClient.getProductDetails(requestDto.getProductId());
            if (product.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Product not found", null));
            }

            Optional<CartItem> existingCartItemOpt = cartItemRepository
                    .findByUserIdAndProductId(requestDto.getUserId(), requestDto.getProductId());

            CartItem cartItem = getCartItem(requestDto, existingCartItemOpt.orElse(null), product.get());

            CartItem savedItem = cartItemRepository.save(cartItem);

            CartItemResponseDto responseDto = CartItemResponseDto.builder()
                    .id(savedItem.getId())
                    .userId(savedItem.getUserId())
                    .productId(savedItem.getProductId())
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

    private static CartItem getCartItem(AddToCartRequestDto requestDto, @Nullable CartItem existingCartItem, ProductCreateResponseDto product) {
        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + requestDto.getQuantity());
            return existingCartItem;
        }

        // Create new cart item
        CartItem newCartItem = new CartItem();
        newCartItem.setUserId(requestDto.getUserId());
        newCartItem.setProductId(requestDto.getProductId());
        newCartItem.setQuantity(requestDto.getQuantity());
        newCartItem.setPrice(product.getPrice());

        return newCartItem;
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> removeFromCart(Long userId, Long productId) {
        try {
            Optional<UserResponseDto> userOpt = userServiceClient.getUser(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "User not found", null));
            }

            Optional<ProductCreateResponseDto> productOpt = productServiceClient.getProductDetails(productId);
            if (productOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Product not found", null));
            }

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
            // Validate user
            Optional<UserResponseDto> userOpt = userServiceClient.getUser(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "User not found or unavailable", null));
            }

            // Fetch user's cart items
            List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

            // Enrich each cart item with product info
            List<CartItemResponseDto> cartItemWithProductInfo = cartItems.stream()
                    .map(item -> {
                        Optional<ProductCreateResponseDto> productOpt = productServiceClient.getProductDetails(item.getProductId());
                        if (productOpt.isEmpty()) {
                            return null;
                        }
                        ProductCreateResponseDto product = productOpt.get();

                        return CartItemResponseDto.builder()
                                .id(item.getId())
                                .userId(item.getUserId())
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .productName(product.getName())
                                .productCategory(product.getCategory())
                                .build();
                    })
                    .filter(Objects::nonNull) // Remove items where product was not found
                    .toList();

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("cartItems", cartItemWithProductInfo);

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
