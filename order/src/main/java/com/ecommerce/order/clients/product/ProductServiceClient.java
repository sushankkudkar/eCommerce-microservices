package com.ecommerce.order.clients.product;

import com.ecommerce.order.dto.ProductCreateResponseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Optional;

public interface ProductServiceClient {

    @GetExchange("/api/product/{id}")
    Optional<ProductCreateResponseDto> getProductDetails(@PathVariable Long id);
}

