package com.smf.controller;

import com.smf.dto.api.ApiResponse;
import com.smf.dto.user.UserRequest;
import com.smf.dto.user.UserResponse;
import com.smf.security.RateLimit;
import com.smf.security.RateLimitKeyType;
import com.smf.service.user.IUserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

  private final IUserService userService;

  @RateLimit(limit = 50, duration = 60, keyType = RateLimitKeyType.USER)
  @PostMapping("/")
  public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserRequest request) {
    UserResponse response = userService.createUser(request);
    return ResponseEntity.ok(new ApiResponse(true, "User created successfully", response));
  }

  @RateLimit(limit = 100, duration = 60, keyType = RateLimitKeyType.USER)
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse> getUser(@PathVariable UUID id) {
    UserResponse response = userService.getUserById(id);
    return ResponseEntity.ok(new ApiResponse(true, "User fetched successfully", response));
  }

  @RateLimit(limit = 100, duration = 60, keyType = RateLimitKeyType.USER)
  @GetMapping("/")
  public ResponseEntity<ApiResponse> getAllUsers() {
    List<UserResponse> users = userService.getAllUsers();
    return ResponseEntity.ok(new ApiResponse(true, "Users fetched successfully", users));
  }

  @RateLimit(limit = 50, duration = 60, keyType = RateLimitKeyType.USER)
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse> updateUser(
      @PathVariable UUID id, @Valid @RequestBody UserRequest request) {
    UserResponse response = userService.updateUser(id, request);
    return ResponseEntity.ok(new ApiResponse(true, "User updated successfully", response));
  }

  @RateLimit(limit = 20, duration = 60, keyType = RateLimitKeyType.USER)
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse> deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
    return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully", null));
  }
}
