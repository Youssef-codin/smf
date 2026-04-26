package com.smf.dto.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceRegisterRequest(
    @NotBlank String smfDeviceLabel,
    @NotBlank String ownerId,
    @NotNull Double lastLocationLat,
    @NotNull Double lastLocationLon) {}

