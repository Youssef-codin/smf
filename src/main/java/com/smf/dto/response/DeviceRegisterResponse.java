package com.smf.dto.response;

public class DeviceRegisterResponse {
    private String deviceId;
    private boolean success;
    private String message;

    public DeviceRegisterResponse(String deviceId, boolean success, String message) {
        this.deviceId = deviceId;
        this.success = success;
        this.message = message;
    }

    // Getters
    public String getDeviceId() { return deviceId; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
