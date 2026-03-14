package com.smf.dto.auth;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;

public record JwtResponse(UUID id, String accessToken, String refreshToken) {
}
