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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
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

  @Value("${google.jwks-uri}")
  private String googleJwksUri;

  @Value("${google.client-ids}")
  private List<String> googleClientIds;

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

  @Override
  @Transactional
  public JwtResponse googleSignIn(String idToken) {
    // 1. Verify the Google ID token against Google's JWKS
    JwtDecoder decoder = googleJwtDecoder();
    Jwt jwt;
    try {
      jwt = decoder.decode(idToken);
    } catch (JwtException e) {
      throw new AppError(HttpStatus.UNAUTHORIZED, "Invalid Google ID token");
    }

    // 2. Validate audience matches one of our allowed client IDs
    List<String> audiences = jwt.getAudience();
    if (audiences == null || audiences.stream().noneMatch(googleClientIds::contains)) {
      throw new AppError(HttpStatus.UNAUTHORIZED, "Google ID token audience mismatch");
    }

    // 3. Extract claims
    String googleId = jwt.getSubject();
    String email = jwt.getClaimAsString("email");
    String name = jwt.getClaimAsString("name");
    String pictureUrl = jwt.getClaimAsString("picture");

    // 4. Find by Google ID first (returning user)
    User user = userRepo.findByGoogleId(googleId).orElseGet(() -> {
      // 5. Try to link an existing local account by email, or create new one
      User u = userRepo.findByEmail(email).orElseGet(() -> {
        User newUser = new User(email, name != null ? name : email, null);
        newUser.setProvider("GOOGLE");
        return newUser;
      });
      u.setGoogleId(googleId);
      u.setProvider("GOOGLE");
      return u;
    });

    // Always keep the profile picture fresh
    user.setPictureUrl(pictureUrl);
    userRepo.save(user);

    return generateTokens(user);
  }

  /** Protected to allow overriding in tests without hitting Google's JWKS endpoint. */
  protected JwtDecoder googleJwtDecoder() {
    return NimbusJwtDecoder.withJwkSetUri(googleJwksUri).build();
  }
}
