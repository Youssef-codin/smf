package com.smf.service.auth;

import com.smf.model.User;
import com.smf.dto.response.api.*;
import com.smf.dto.request.auth.*;

public interface IAuthService {
    JwtResponse login(LoginRequest req);

    User register(RegisterRequest req);
}
