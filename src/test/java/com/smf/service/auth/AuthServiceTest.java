package com.smf.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.smf.dto.auth.JwtResponse;
import com.smf.dto.auth.LoginRequest;
import com.smf.dto.auth.RegisterRequest;
import com.smf.model.User;
import com.smf.repo.UserRepository;
import com.smf.security.AppUserDetails;
import com.smf.security.JwtUtils;
import com.smf.util.AppError;
import java.time.LocalDateTime;
import java.util.Optional;
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

  
    lenient().when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);

    lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
    lenient().when(userDetails.getUsername()).thenReturn(user.getEmail());
    
    lenient().when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    lenient().when(userRepo.save(any(User.class))).thenReturn(user);
    lenient().when(jwtUtils.generateTokenFromUserDetails(any(AppUserDetails.class))).thenReturn("mocked-jwt"); 

    JwtResponse response = authService.login(req);

    assertNotNull(response);
    assertEquals("mocked-jwt", response.accessToken());
    assertNotNull(response.refreshToken());
verify(userRepo, times(1)).save(any(User.class)); // refresh token save
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

  @Test
  void refresh_shouldReturnNewTokens_whenValidRefreshToken() {
    String tokenId = UUID.randomUUID().toString();
    String refreshToken = tokenId + ".abc123";
    user.setRefreshTokenId(tokenId);
    user.setRefreshTokenHash("hashedToken");
    user.setRefreshTokenExpiry(LocalDateTime.now().plusHours(1));

    when(userRepo.findByRefreshTokenId(tokenId)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(refreshToken, "hashedToken")).thenReturn(true);
    when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
    when(jwtUtils.generateTokenFromUserDetails(any(AppUserDetails.class))).thenReturn("new-jwt");

    JwtResponse response = authService.refresh(refreshToken);

    assertNotNull(response);
    assertEquals("new-jwt", response.accessToken());
    assertNotNull(response.refreshToken());
    verify(userRepo, times(2)).save(any(User.class));
  }

  @Test
  void refresh_shouldThrowException_whenInvalidTokenFormat() {
    String invalidToken = "no-dot-here";

    AppError exception = assertThrows(AppError.class, () -> authService.refresh(invalidToken));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertEquals("Invalid refresh token format", exception.getMessage());
  }

  @Test
  void refresh_shouldThrowException_whenTokenNotFound() {
    String tokenId = UUID.randomUUID().toString();
    String refreshToken = tokenId + ".abc123";

    when(userRepo.findByRefreshTokenId(tokenId)).thenReturn(Optional.empty());

    AppError exception = assertThrows(AppError.class, () -> authService.refresh(refreshToken));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertEquals("Invalid refresh token", exception.getMessage());
  }

  @Test
  void refresh_shouldThrowException_whenRefreshTokenIdIsNull() {
    String tokenId = UUID.randomUUID().toString();
    String refreshToken = tokenId + ".abc123";
    user.setRefreshTokenId(null);
    user.setRefreshTokenHash("hashedToken");

    when(userRepo.findByRefreshTokenId(tokenId)).thenReturn(Optional.of(user));

    AppError exception = assertThrows(AppError.class, () -> authService.refresh(refreshToken));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertEquals("Invalid or expired refresh token", exception.getMessage());
  }

  @Test
  void refresh_shouldThrowException_whenRefreshTokenExpiryIsNull() {
    String tokenId = UUID.randomUUID().toString();
    String refreshToken = tokenId + ".abc123";
    user.setRefreshTokenId(tokenId);
    user.setRefreshTokenHash("hashedToken");
    user.setRefreshTokenExpiry(null);

    when(userRepo.findByRefreshTokenId(tokenId)).thenReturn(Optional.of(user));

    AppError exception = assertThrows(AppError.class, () -> authService.refresh(refreshToken));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertEquals("Invalid or expired refresh token", exception.getMessage());
  }

  @Test
  void refresh_shouldThrowException_whenTokenExpired() {
    String tokenId = UUID.randomUUID().toString();
    String refreshToken = tokenId + ".abc123";
    user.setRefreshTokenId(tokenId);
    user.setRefreshTokenHash("hashedToken");
    user.setRefreshTokenExpiry(LocalDateTime.now().minusHours(1));

    when(userRepo.findByRefreshTokenId(tokenId)).thenReturn(Optional.of(user));

    AppError exception = assertThrows(AppError.class, () -> authService.refresh(refreshToken));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertEquals("Invalid or expired refresh token", exception.getMessage());
  }

  @Test
  void refresh_shouldThrowException_whenTokenHashMismatch() {
    String tokenId = UUID.randomUUID().toString();
    String refreshToken = tokenId + ".abc123";
    user.setRefreshTokenId(tokenId);
    user.setRefreshTokenHash("hashedToken");
    user.setRefreshTokenExpiry(LocalDateTime.now().plusHours(1));

    when(userRepo.findByRefreshTokenId(tokenId)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(refreshToken, "hashedToken")).thenReturn(false);

    AppError exception = assertThrows(AppError.class, () -> authService.refresh(refreshToken));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertEquals("Invalid or expired refresh token", exception.getMessage());
  }

  @Test
  void logout_shouldClearTokens_whenValidRefreshToken() {
    String tokenId = UUID.randomUUID().toString();
    String refreshToken = tokenId + ".abc123";
    user.setRefreshTokenId(tokenId);
    user.setRefreshTokenHash("hashedToken");

    when(userRepo.findByRefreshTokenId(tokenId)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(refreshToken, "hashedToken")).thenReturn(true);
    when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    assertDoesNotThrow(() -> authService.logout(refreshToken));

    verify(userRepo).save(any(User.class));
  }

  @Test
  void logout_shouldThrowException_whenInvalidTokenFormat() {
    String invalidToken = "no-dot-here";

    AppError exception = assertThrows(AppError.class, () -> authService.logout(invalidToken));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertEquals("Invalid refresh token format", exception.getMessage());
  }

  @Test
  void logout_shouldThrowException_whenTokenNotFound() {
    String tokenId = UUID.randomUUID().toString();
    String refreshToken = tokenId + ".abc123";

    when(userRepo.findByRefreshTokenId(tokenId)).thenReturn(Optional.empty());

    AppError exception = assertThrows(AppError.class, () -> authService.logout(refreshToken));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertEquals("Invalid refresh token", exception.getMessage());
  }

  @Test
  void logout_shouldThrowException_whenRefreshTokenIdIsNull() {
    String tokenId = UUID.randomUUID().toString();
    String refreshToken = tokenId + ".abc123";
    user.setRefreshTokenId(null);
    user.setRefreshTokenHash("hashedToken");

    when(userRepo.findByRefreshTokenId(tokenId)).thenReturn(Optional.of(user));

    AppError exception = assertThrows(AppError.class, () -> authService.logout(refreshToken));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertEquals("Invalid refresh token", exception.getMessage());
  }

  @Test
  void logout_shouldThrowException_whenTokenHashMismatch() {
    String tokenId = UUID.randomUUID().toString();
    String refreshToken = tokenId + ".abc123";
    user.setRefreshTokenId(tokenId);
    user.setRefreshTokenHash("hashedToken");

    when(userRepo.findByRefreshTokenId(tokenId)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(refreshToken, "hashedToken")).thenReturn(false);

    AppError exception = assertThrows(AppError.class, () -> authService.logout(refreshToken));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertEquals("Invalid refresh token", exception.getMessage());
  }
}
