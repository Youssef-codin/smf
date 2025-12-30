package com.smf.service.device;

import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.dto.device.DeviceResponse;
import com.smf.dto.device.DeviceTestRequest;
import java.util.UUID;

public interface IDeviceService {
  DeviceResponse registerDevice(DeviceRegisterRequest request);

  DeviceResponse getDeviceById(UUID deviceId);

  void test(DeviceTestRequest request);
}
