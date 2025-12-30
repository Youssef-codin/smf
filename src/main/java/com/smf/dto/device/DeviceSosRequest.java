package com.smf.dto.device;

import jakarta.validation.constraints.NotBlank;

public class DeviceSosRequest {

    @NotBlank
    private String macAddress;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}