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
    private final RefreshTokenService refreshService;

    @Override
    public JwtResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        AppUserDetails userDetails = (AppUserDetails) auth.getPrincipal();
        String jwt = jwtUtils.generateToken(auth);
        String refreshToken = refreshService.createRefreshToken(userDetails.getUser());
        return new JwtResponse(userDetails.getId(), jwt + ":" + refreshToken);
    }

    @Override
    public User register(RegisterRequest req) {
        boolean alreadyExist = userRepo.existsByEmail(req.email());
        if (alreadyExist) throw new AppError(HttpStatus.CONFLICT, "Email Already Used");

        User newUser = new User(req.email(), req.username(), passwordEncoder.encode(req.password()));
        User saved = userRepo.save(newUser);
        refreshService.createRefreshToken(saved);
        return saved;
    }

    @Override
    public JwtResponse refresh(String refreshToken) {
        var token = refreshService.verifyToken(refreshToken);
        User user = token.getUser();
        refreshService.deleteByUser(user.getId());
        String newRefresh = refreshService.createRefreshToken(user);
        String jwt = jwtUtils.generateToken(new AppUserDetails(user));
        return new JwtResponse(user.getId(), jwt + ":" + newRefresh);
    }
}
