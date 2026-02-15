package com.smf.dto.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;

import com.smf.model.enums.DeviceStatus;
import lombok.Data;

@Data
public class DeviceRegisterRequest {

    @NotBlank
    private String macAddress;

    @NotBlank
    private String ownerId;

    @NotNull
    private Double lastLocationLat;

    @NotNull
    private Double lastLocationLon;

    @NotNull
    private Timestamp lastSeenTimestamp;

    @NotNull
    private DeviceStatus status;
}
