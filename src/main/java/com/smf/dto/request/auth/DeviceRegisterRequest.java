package com.smf.dto.request.auth;

import java.util.UUID;

public class DeviceRegisterRequest {
    private String deviceName;
    private String serialNumber;
    private Double lastLocationLat;
    private Double lastLocationLon;
    private UUID ownerId;  // <-- required now

    // Getters and setters
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public Double getLastLocationLat() { return lastLocationLat; }
    public void setLastLocationLat(Double lastLocationLat) { this.lastLocationLat = lastLocationLat; }
    public Double getLastLocationLon() { return lastLocationLon; }
    public void setLastLocationLon(Double lastLocationLon) { this.lastLocationLon = lastLocationLon; }
    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
}
