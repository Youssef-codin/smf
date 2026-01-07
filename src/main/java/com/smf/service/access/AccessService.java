package com.smf.service.access;

import com.smf.dto.device.DeviceEventRequest;
import com.smf.model.enums.EventTypes;
import com.smf.util.LogEvent;
import org.springframework.stereotype.Service;

@Service
public class AccessService implements IAccessService {

  @Override
  public void processEvent(DeviceEventRequest req) {
    switch (req.event()) {
      case DEVICE_OFFLINE -> handleOffline(req.macAddress());
      case DEVICE_ONLINE -> handleOnline(req.macAddress());
      case SOS_TRIGGERED -> handleSos(req.macAddress());
      case ACCESS_DENIED -> handleDenied(req.macAddress());
      case ACCESS_GRANTED -> handleGranted(req.macAddress());
      case TESTING -> handleTest(req.macAddress());
    }
  }

  // NOTE:Could add business logic to these if needed
  @LogEvent(eventType = EventTypes.TESTING)
  private void handleTest(String macAddress) {}

  @LogEvent(eventType = EventTypes.ACCESS_DENIED)
  private void handleDenied(String macAddress) {}

  @LogEvent(eventType = EventTypes.DEVICE_ONLINE)
  private void handleOnline(String macAddress) {}

  @LogEvent(eventType = EventTypes.ACCESS_GRANTED)
  private void handleGranted(String macAddress) {}

  @LogEvent(eventType = EventTypes.SOS_TRIGGERED)
  private void handleSos(String macAddress) {}

  @LogEvent(eventType = EventTypes.DEVICE_OFFLINE)
  private void handleOffline(String macAddress) {}
}

