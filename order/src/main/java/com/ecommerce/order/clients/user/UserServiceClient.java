package com.ecommerce.order.clients.user;

import com.ecommerce.order.dto.UserResponseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Optional;

public interface UserServiceClient {

    @GetExchange("/api/user/{userId}")
    Optional<UserResponseDto> getUser(@PathVariable String userId);
}
