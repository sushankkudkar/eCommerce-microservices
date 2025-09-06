package com.ecommerce.product.controller;

import com.ecommerce.product.dto.*;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/simulate")
    public ResponseEntity<String> simulateFailure(@RequestParam(defaultValue = "false") boolean fail) {
        if(fail) {
            throw new RuntimeException("Simulated Failure For Testing");
        }
        return ResponseEntity.ok("Product Service is OK");
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCreateResponseDto>> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
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
