package com.smf.dto.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeviceTestRequest {

    @NotBlank
    private String macAddress;

    @NotNull
    private Double lat;

    @NotNull
    private Double lon;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String mac_address) {
        this.macAddress = mac_address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
