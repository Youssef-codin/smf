package com.smf.service.auth;

import com.smf.dto.auth.JwtResponse;
import com.smf.dto.auth.LoginRequest;
import com.smf.dto.auth.RegisterRequest;
import com.smf.model.User;
import com.smf.repo.UserRepository;
import com.smf.security.AppUserDetails;
import com.smf.security.JwtUtils;
import com.smf.util.AppError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService implements IAuthService {

  private final UserRepository userRepo;
  private final BCryptPasswordEncoder passwordEncoder;
  private final AuthenticationManager authManager;
  private final JwtUtils jwtUtils;

  @Override
  public JwtResponse login(LoginRequest req) {
    Authentication auth =
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.email(), req.password()));
    SecurityContextHolder.getContext().setAuthentication(auth);
    String jwt = jwtUtils.generateToken(auth);
    AppUserDetails userDetails = (AppUserDetails) auth.getPrincipal();

    return new JwtResponse(userDetails.getId(), jwt);
  }

  @Override
  public User register(RegisterRequest req) {
    boolean alreadyExist = userRepo.existsByEmail(req.email());

    if (alreadyExist) throw new AppError(HttpStatus.CONFLICT, "Email Already Used");

    User newUser = new User(req.email(), req.username(), passwordEncoder.encode(req.password()));

    return userRepo.save(newUser);
  }
}
