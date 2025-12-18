package com.smf.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeviceRegisterRequest {

    @NotBlank
    private String deviceId;

    @NotBlank
    private String deviceName;

    @NotBlank
    private String ownerId; 

    @NotNull
    private Double lastLocationLat;

    @NotNull
    private Double lastLocationLon;

    @NotBlank
    private String serialNumber;

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public Double getLastLocationLat() { return lastLocationLat; }
    public void setLastLocationLat(Double lastLocationLat) { this.lastLocationLat = lastLocationLat; }

    public Double getLastLocationLon() { return lastLocationLon; }
    public void setLastLocationLon(Double lastLocationLon) { this.lastLocationLon = lastLocationLon; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
}
