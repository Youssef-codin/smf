package com.smf.dto.device;

import jakarta.validation.constraints.NotBlank;

public record DeviceOfflineRequest(@NotBlank String MacAddress) {}
