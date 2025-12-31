package com.smf.service.device;

import java.util.List;
import java.util.UUID;

import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.dto.device.DeviceResponse;

public interface IDeviceService {

    DeviceResponse registerDevice(DeviceRegisterRequest request);

    DeviceResponse getDeviceById(UUID deviceId);

    List<DeviceResponse> getAllDevices();

    DeviceResponse updateDevice(UUID deviceId, DeviceRegisterRequest request);

    void deleteDevice(UUID deviceId);
}
