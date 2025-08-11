package com.ecommerce.user.controller;

import com.ecommerce.user.dto.*;
import com.ecommerce.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<UserCreateResponseDto>> createUser(@RequestBody UserCreateRequestDto request) {
        return userService.createUser(request);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@PathVariable String id, @RequestBody UserUpdateRequestDto request) {
        return userService.updateUser(id, request);
    }
}
