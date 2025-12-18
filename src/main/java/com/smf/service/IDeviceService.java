package com.smf.service;

import com.smf.dto.request.auth.DeviceRegisterRequest;
import com.smf.dto.response.DeviceRegisterResponse;

public interface IDeviceService {
    DeviceRegisterResponse registerDevice(DeviceRegisterRequest request);
}
