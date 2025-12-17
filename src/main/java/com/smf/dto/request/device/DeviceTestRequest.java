package com.smf.dto.request.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeviceTestRequest {

    @NotBlank
    private String device_id;

    @NotNull
    private Double lat;

    @NotNull
    private Double lon;

    private String status;

    // Getters & Setters
    public String getDevice_id() { return device_id; }
    public void setDevice_id(String device_id) { this.device_id = device_id; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
