package com.smf.service.auth;

import com.smf.model.RefreshToken;
import com.smf.model.User;
import com.smf.repo.RefreshTokenRepository;
import com.smf.util.AppError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final BCryptPasswordEncoder encoder;

    private final long refreshExpirationMs = 1000L * 60 * 60 * 24 * 7;

    public String createRefreshToken(User user) {
        String rawToken = UUID.randomUUID().toString();
        String hashed = encoder.encode(rawToken);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(hashed);
        token.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));

        repo.save(token);

        return rawToken;
    }

    public RefreshToken verifyToken(String rawToken) {
        return repo.findAll()
                .stream()
                .filter(t -> encoder.matches(rawToken, t.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> new AppError(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
    }

    public void deleteByUser(UUID userId) {
        repo.deleteByUserId(userId);
    }
}
