package com.api_implementation.user.contoller;

import com.api_implementation.user.dto.UserRequest;
import com.api_implementation.user.dto.UserResponse;
import com.api_implementation.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.fetchAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return userService.fetchUser(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(userRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updatedUser(@PathVariable Long id, @RequestBody UserRequest updatedUserRequest) {
        return userService.updateUser(id, updatedUserRequest).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
