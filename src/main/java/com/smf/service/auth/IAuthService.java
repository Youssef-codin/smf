package com.smf.service.auth;

import com.smf.dto.auth.JwtResponse;
import com.smf.dto.auth.LoginRequest;
import com.smf.dto.auth.RegisterRequest;
import com.smf.model.User;

public interface IAuthService {
  /**
   * Authenticates user with email/password and returns JWT access + refresh tokens.
   */
  JwtResponse login(LoginRequest req);

  /**
   * Registers new user if email not exists.
   */
  User register(RegisterRequest req);

  /**
   * Refreshes access token using valid refresh token, invalidates old refresh token.
   */
  JwtResponse refresh(String refreshToken);

  /**
   * Invalidates refresh token to logout user session.
   */
  void logout(String refreshToken);
}

