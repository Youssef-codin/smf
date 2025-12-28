package com.smf.dto.device;

import java.sql.Timestamp;
import java.util.UUID;

import com.smf.model.enums.DeviceStatus;

public record DeviceResponse(
        UUID id,
        String macAddress,
        UUID ownerId,
        Double lastLocationLat,
        Double lastLocationLon,
        Timestamp lastSeenTimestamp,
        DeviceStatus status) {
}
