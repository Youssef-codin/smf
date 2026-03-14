package com.smf.repo;

import com.smf.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    List<RefreshToken> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
