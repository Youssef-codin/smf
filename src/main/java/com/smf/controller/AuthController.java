package com.smf.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smf.dto.api.ApiResponse;
import com.smf.dto.auth.JwtResponse;
import com.smf.dto.auth.LoginRequest;
import com.smf.dto.auth.RegisterRequest;
import com.smf.model.User;
import com.smf.service.auth.IAuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {
	private final IAuthService authService;

	public AuthController(IAuthService authService) {
		this.authService = authService;
	}

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

	@PreAuthorize("hasAuthority('USER')")
	@GetMapping("/isUser")
	public ResponseEntity<ApiResponse> testUser() {
		return ResponseEntity.ok(new ApiResponse(true, "You are an Authenticated user", null));
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/isAdmin")
	public ResponseEntity<ApiResponse> testAdmin() {
		return ResponseEntity.ok(new ApiResponse(true, "You are an Authenticated user", null));
	}

	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@GetMapping("/isAuthenticated")
	public ResponseEntity<ApiResponse> testAuth() {
		return ResponseEntity.ok(new ApiResponse(true, "You are an Authenticated user or admin", null));
	}
}
