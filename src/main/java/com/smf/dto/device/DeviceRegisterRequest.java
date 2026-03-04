package com.smf.dto.device;

import com.smf.model.enums.DeviceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor 
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