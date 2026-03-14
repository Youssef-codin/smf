package com.smf.controller;

import com.smf.dto.api.ApiResponse;
import com.smf.dto.auth.JwtResponse;
import com.smf.dto.auth.LoginRequest;
import com.smf.dto.auth.LogoutRequest;
import com.smf.dto.auth.RefreshRequest;
import com.smf.dto.auth.RegisterRequest;
import com.smf.model.User;
import com.smf.service.auth.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {
  private final IAuthService authService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest req) {
    JwtResponse jwtResponse = authService.login(req);
    return ResponseEntity.ok(new ApiResponse(true, "Here's your token.", jwtResponse));
  }

@PostMapping("/register")
  public ResponseEntity<ApiResponse> addUser(@Valid @RequestBody RegisterRequest req) {
    User user = authService.register(req);
    return ResponseEntity.ok(new ApiResponse(true, "User added successfully", user));
  }

@PostMapping("/refresh")
  public ResponseEntity<ApiResponse> refresh(@Valid @RequestBody RefreshRequest req) {
    JwtResponse tokens = authService.refresh(req.refreshToken());
    return ResponseEntity.ok(new ApiResponse(true, "Tokens refreshed", tokens));
  }
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse> logout(@Valid @RequestBody LogoutRequest req) {
    authService.logout(req.refreshToken());
    return ResponseEntity.ok(new ApiResponse(true, "Logged out successfully", null));
  }
}
