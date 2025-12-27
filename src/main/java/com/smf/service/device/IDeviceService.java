package com.smf.service.device;

import java.util.UUID;

import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.dto.device.DeviceResponse;

public interface IDeviceService {
    DeviceResponse registerDevice(DeviceRegisterRequest request);
    DeviceResponse getDeviceById(UUID deviceId);
}
