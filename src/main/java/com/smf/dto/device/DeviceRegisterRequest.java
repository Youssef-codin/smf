package com.smf.dto.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;

import com.smf.model.enums.DeviceStatus;

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

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Double getLastLocationLat() {
        return lastLocationLat;
    }

    public void setLastLocationLat(Double lastLocationLat) {
        this.lastLocationLat = lastLocationLat;
    }

    public Double getLastLocationLon() {
        return lastLocationLon;
    }

    public void setLastLocationLon(Double lastLocationLon) {
        this.lastLocationLon = lastLocationLon;
    }

    public Timestamp getLastSeenTimestamp() {
        return lastSeenTimestamp;
    }

    public void setLastSeenTimestamp(Timestamp lastSeenTimestamp) {
        this.lastSeenTimestamp = lastSeenTimestamp;
    }
}
