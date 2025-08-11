package com.ecommerce.product.controller;

import com.ecommerce.product.dto.*;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProductCreateResponseDto>> createProduct(@RequestBody ProductCreateRequestDto product) {
        return productService.createProduct(product);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<ProductCreateResponseDto>> updateProduct(@RequestBody ProductUpdateRequestDto requestDto) {
        return productService.updateProduct(requestDto);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductCreateResponseDto>>> getAllProducts() {
        return productService.getAllProducts();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        return productService.deleteProductById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductCreateResponseDto>>> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }
}
