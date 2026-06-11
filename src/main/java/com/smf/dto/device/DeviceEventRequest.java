package com.smf.dto.device;

import com.smf.model.enums.EventTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record DeviceEventRequest(
    @NotBlank String macAddress,
    @NotNull EventTypes event,
    Map<String, Object> metadata) {}

