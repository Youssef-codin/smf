package com.smf.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.smf.dto.auth.LoginRequest;
import com.smf.dto.auth.RegisterRequest;
import com.smf.model.User;
import com.smf.repo.UserRepository;
import com.smf.security.AppUserDetails;
import com.smf.security.JwtUtils;
import com.smf.util.AppError;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepo;

  @Mock private BCryptPasswordEncoder passwordEncoder;

  @Mock private AuthenticationManager authManager;

  @Mock private JwtUtils jwtUtils;

  @InjectMocks private AuthService authService;

  private User user;

  @BeforeEach
  void setup() {
    user = new User("test@mail.com", "testUser", "encodedPass");
    user.setId(UUID.randomUUID());
  }

  @Test
  void register_shouldCreateUser_whenEmailNotExists() {
    RegisterRequest req = new RegisterRequest("test@mail.com", "testUser", "password");

    when(userRepo.existsByEmail(req.email())).thenReturn(false);
    when(passwordEncoder.encode(req.password())).thenReturn("encodedPass");
    when(userRepo.save(any(User.class))).thenReturn(user);

    User savedUser = authService.register(req);

    assertNotNull(savedUser);
    assertEquals("test@mail.com", savedUser.getEmail());

    verify(userRepo, times(1)).save(any(User.class));
  }

  @Test
  void register_shouldThrowException_whenEmailExists() {
    RegisterRequest req = new RegisterRequest("test@mail.com", "testUser", "password");

    when(userRepo.existsByEmail(req.email())).thenReturn(true);

    AppError exception = assertThrows(AppError.class, () -> authService.register(req));

    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }

  @Test
  void login_shouldReturnJwtResponse_whenCredentialsValid() {
    LoginRequest req = new LoginRequest("test@mail.com", "password");

    Authentication authentication = mock(Authentication.class);
    AppUserDetails userDetails = mock(AppUserDetails.class);

    when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);

    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getId()).thenReturn(UUID.randomUUID());
    when(jwtUtils.generateToken(authentication)).thenReturn("mocked-jwt");

    var response = authService.login(req);

    assertNotNull(response);
    assertEquals("mocked-jwt", response.token());
  }

  @Test
  void login_shouldThrowException_whenInvalidCredentials() {
    LoginRequest req = new LoginRequest("test@mail.com", "wrongpassword");

    when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));

    assertThrows(org.springframework.security.authentication.BadCredentialsException.class, () -> authService.login(req));
  }

  @Test
  void login_shouldThrowException_whenEmailNotExists() {
    LoginRequest req = new LoginRequest("nonexistent@mail.com", "password");

    when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));

    assertThrows(org.springframework.security.authentication.BadCredentialsException.class, () -> authService.login(req));
  }
}

