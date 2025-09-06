package com.ecommerce.product.service;

import com.ecommerce.product.dto.*;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ResponseEntity<ApiResponse<ProductCreateResponseDto>> createProduct(ProductCreateRequestDto reqProduct) {
        Product product = new Product();
        product.setName(reqProduct.getName());
        product.setPrice(reqProduct.getPrice());
        product.setCategory(reqProduct.getCategory());

        Product savedProduct = productRepository.save(product);
        ProductCreateResponseDto responseDto = mapToProductCreateResponse(savedProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ProductCreateResponseDto>builder()
                        .success(true)
                        .message("Product created successfully")
                        .data(responseDto)
                        .build()
        );
    }

    public ResponseEntity<ApiResponse<ProductCreateResponseDto>> updateProduct(ProductUpdateRequestDto requestDto) {
        Optional<Product> optionalProduct = productRepository.findById(requestDto.getId());

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<ProductCreateResponseDto>builder()
                            .success(false)
                            .message("Product not found")
                            .build()
            );
        }

        Product product = optionalProduct.get();
        product.setName(requestDto.getName());
        product.setDescription(requestDto.getDescription());
        product.setPrice(requestDto.getPrice());
        product.setQuantity(requestDto.getQuantity());
        product.setCategory(requestDto.getCategory());
        product.setImageUrl(requestDto.getImageUrl());
        product.setActive(requestDto.getActive());

        Product updatedProduct = productRepository.save(product);
        ProductCreateResponseDto responseDto = mapToProductCreateResponse(updatedProduct);

        return ResponseEntity.ok(
                ApiResponse.<ProductCreateResponseDto>builder()
                        .success(true)
                        .message("Product updated successfully")
                        .data(responseDto)
                        .build()
        );
    }

    public ResponseEntity<ApiResponse<List<ProductCreateResponseDto>>> getAllProducts() {
        List<Product> products = productRepository.findAll();

        List<ProductCreateResponseDto> responseList = products.stream()
                .map(this::mapToProductCreateResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<ProductCreateResponseDto>>builder()
                        .success(true)
                        .message("Products fetched successfully")
                        .data(responseList)
                        .build()
        );
    }

    public ResponseEntity<ApiResponse<String>> deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<String>builder()
                            .success(false)
                            .message("Product not found")
                            .build()
            );
        }

        productRepository.deleteById(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Product deleted successfully")
                        .data("Deleted product with ID: " + id)
                        .build()
        );
    }

    public ResponseEntity<ApiResponse<List<ProductCreateResponseDto>>> searchProducts(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword);

        List<ProductCreateResponseDto> responseList = products.stream()
                .map(this::mapToProductCreateResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<ProductCreateResponseDto>>builder()
                        .success(true)
                        .message("Products found")
                        .data(responseList)
                        .build()
        );
    }

    private ProductCreateResponseDto mapToProductCreateResponse(Product savedProduct) {
        return ProductCreateResponseDto.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .price(savedProduct.getPrice())
                .category(savedProduct.getCategory())
                .build();
    }

    public ResponseEntity<ApiResponse<ProductCreateResponseDto>> getProductById(Long id) {
        Optional<Product> optionalProduct = productRepository.findByIdAndActiveTrue(id);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.ok(
                    ApiResponse.<ProductCreateResponseDto>builder()
                            .success(true)
                            .message("No product found")
                            .data(null)
                            .build()
            );
        }

        ProductCreateResponseDto responseDto = mapToProductCreateResponse(optionalProduct.get());

        return ResponseEntity.ok(
                ApiResponse.<ProductCreateResponseDto>builder()
                        .success(true)
                        .message("Product found")
                        .data(responseDto)
                        .build()
        );
    }
}

