package com.ecommerce.user.service;

import com.ecommerce.user.dto.*;
import com.ecommerce.user.model.*;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDto> responseList = users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Users fetched successfully", responseList)
        );
    }

    public ResponseEntity<ApiResponse<UserCreateResponseDto>> createUser(UserCreateRequestDto request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole() != null ? request.getRole() : UserRole.CUSTOMER);

        if (request.getAddress() != null) {
            user.setAddress(mapToAddressEntity(request.getAddress()));
        }

        User savedUser = userRepository.save(user);

        UserCreateResponseDto response = new UserCreateResponseDto(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole(),
                mapToAddressDto(savedUser.getAddress())
        );

        return new ResponseEntity<>(
                new ApiResponse<>(true, "User created successfully", response),
                HttpStatus.CREATED
        );
    }

    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional
                .map(user -> ResponseEntity.ok(
                        new ApiResponse<>(true, "User fetched successfully", mapToUserResponse(user))))
                .orElseGet(() -> new ResponseEntity<>(
                        new ApiResponse<>(false, "User not found", null),
                        HttpStatus.NOT_FOUND
                ));
    }

    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(String id, UserUpdateRequestDto request) {
        Optional<User> dbUser = userRepository.findById(id);

        if (dbUser.isPresent()) {
            User existingUser = dbUser.get();

            if (request.getFirstName() != null) existingUser.setFirstName(request.getFirstName());
            if (request.getLastName() != null) existingUser.setLastName(request.getLastName());
            if (request.getEmail() != null) existingUser.setEmail(request.getEmail());
            if (request.getPhone() != null) existingUser.setPhone(request.getPhone());
            if (request.getRole() != null) existingUser.setRole(request.getRole());

            if (request.getAddress() != null) {
                existingUser.setAddress(mapToAddressEntity(request.getAddress()));
            } else {
                existingUser.setAddress(null);
            }

            User updatedUser = userRepository.save(existingUser);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "User updated successfully", mapToUserResponse(updatedUser))
            );
        } else {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "User not found", null),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    private UserResponseDto mapToUserResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                mapToAddressDto(user.getAddress())
        );
    }

    private Address mapToAddressEntity(AddressDto dto) {
        if (dto == null) return null;

        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setZipcode(dto.getZipcode());
        return address;
    }

    private AddressDto mapToAddressDto(Address address) {
        if (address == null) return null;

        return new AddressDto(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getZipcode()
        );
    }
}
