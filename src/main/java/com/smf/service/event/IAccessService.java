package com.smf.service.access;

import com.smf.dto.device.DeviceEventRequest;

public interface IAccessService {
  void processEvent(DeviceEventRequest req);
}
