package com.smf.service.device;

import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.dto.device.DeviceResponse;
import com.smf.dto.device.DeviceTestRequest;
import java.util.List;
import java.util.UUID;

public interface IDeviceService {
  DeviceResponse registerDevice(DeviceRegisterRequest request);

  DeviceResponse getDeviceById(UUID deviceId);

  List<DeviceResponse> getAllDevices();

  DeviceResponse updateDevice(UUID deviceId, DeviceRegisterRequest request);

  void deleteDevice(UUID deviceId);

  DeviceResponse handleSos(String macAddress);

  DeviceResponse handleOffline(String macAddress);

  void test(DeviceTestRequest request);
}
