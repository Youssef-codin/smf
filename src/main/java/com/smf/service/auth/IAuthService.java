package com.smf.service.auth;

import com.smf.dto.auth.JwtResponse;
import com.smf.dto.auth.LoginRequest;
import com.smf.dto.auth.RegisterRequest;
import com.smf.model.User;

public interface IAuthService {
  JwtResponse login(LoginRequest req);

  User register(RegisterRequest req);
}
