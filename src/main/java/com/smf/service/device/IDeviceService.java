package com.smf.service.device;

import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.model.Device;

public interface IDeviceService {
    Device registerDevice(DeviceRegisterRequest request);
}
