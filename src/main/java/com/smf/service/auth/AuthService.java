package com.smf.service.auth;

import com.smf.dto.auth.JwtResponse;
import com.smf.dto.auth.LoginRequest;
import com.smf.dto.auth.RegisterRequest;
import com.smf.model.User;
import com.smf.repo.UserRepository;
import com.smf.security.AppUserDetails;
import com.smf.security.JwtUtils;
import com.smf.util.AppError;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService implements IAuthService {

  private final UserRepository userRepo;
  private final BCryptPasswordEncoder passwordEncoder;
  private final AuthenticationManager authManager;
  private final JwtUtils jwtUtils;

  @Value("${jwt.refresh.expiration:1209600000}")
  private long refreshTokenExpiryMs;

  private String generateRefreshToken(User user) {
    String refreshTokenId = UUID.randomUUID().toString();

    SecureRandom secureRandom = new SecureRandom();
    byte[] randomBytes = new byte[32];
    secureRandom.nextBytes(randomBytes);
    String randomSuffix = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

    String refreshToken = refreshTokenId + "." + randomSuffix;
    String hash = passwordEncoder.encode(refreshToken);

    user.setRefreshTokenId(refreshTokenId);
    user.setRefreshTokenHash(hash);
    user.setRefreshTokenExpiry(LocalDateTime.now().plusSeconds(refreshTokenExpiryMs / 1000));
    userRepo.save(user);

    return refreshToken;
  }

  private JwtResponse generateTokens(User user) {
    AppUserDetails userDetails = AppUserDetails.buildUserDetails(user);
    String jwt = jwtUtils.generateTokenFromUserDetails(userDetails);
    String refreshToken = generateRefreshToken(user);
    return new JwtResponse(user.getId(), jwt, refreshToken);
  }

  @Override
  public JwtResponse login(LoginRequest req) {
    Authentication auth =
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.email(), req.password()));
    SecurityContextHolder.getContext().setAuthentication(auth);
    AppUserDetails userDetails = (AppUserDetails) auth.getPrincipal();
    User user =
        userRepo
            .findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new AppError(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    return generateTokens(user);
  }

  @Override
  public User register(RegisterRequest req) {
    boolean alreadyExist = userRepo.existsByEmail(req.email());

    if (alreadyExist) throw new AppError(HttpStatus.CONFLICT, "Email Already Used");

    User newUser = new User(req.email(), req.username(), passwordEncoder.encode(req.password()));

    User savedUser = userRepo.save(newUser);
    return savedUser;
  }

  @Transactional
  public JwtResponse refresh(String refreshToken) {
    String[] parts = refreshToken.split("\\.", 2);
    if (parts.length != 2) {
      throw new AppError(HttpStatus.UNAUTHORIZED, "Invalid refresh token format");
    }
    String tokenId = parts[0];
    User user =
        userRepo
            .findByRefreshTokenId(tokenId)
            .orElseThrow(() -> new AppError(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
    if (user.getRefreshTokenHash() == null
        || user.getRefreshTokenExpiry() == null
        || user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())
        || !passwordEncoder.matches(refreshToken, user.getRefreshTokenHash())) {
      throw new AppError(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
    }
    user.setRefreshTokenId(null);
    user.setRefreshTokenHash(null);
    user.setRefreshTokenExpiry(null);
    userRepo.save(user);
    return generateTokens(user);
  }

  @Transactional
  public void logout(String refreshToken) {
    String[] parts = refreshToken.split("\\.", 2);
    if (parts.length != 2) {
      throw new AppError(HttpStatus.UNAUTHORIZED, "Invalid refresh token format");
    }
    String tokenId = parts[0];
    User user =
        userRepo
            .findByRefreshTokenId(tokenId)
            .orElseThrow(() -> new AppError(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
    if (user.getRefreshTokenHash() == null
        || !passwordEncoder.matches(refreshToken, user.getRefreshTokenHash())) {
      throw new AppError(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    }
    user.setRefreshTokenId(null);
    user.setRefreshTokenHash(null);
    user.setRefreshTokenExpiry(null);
    userRepo.save(user);
  }
}

