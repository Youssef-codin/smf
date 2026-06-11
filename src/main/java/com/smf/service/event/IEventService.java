package com.smf.service.event;

import com.smf.dto.zone.ZoneAccessResult;
import com.smf.model.Event;
import java.util.List;

public interface IEventService {
  List<Event> getEvents(int since);

  List<Event> getAllEvents();

  void handleTest(String macAddress, String metadata);

  void handleDenied(String macAddress, String metadata);

  void handleOnline(String macAddress, String metadata);

  void handleGranted(String macAddress, String metadata);

  void handleSos(String macAddress, String metadata);

  void handleOffline(String macAddress, String metadata);

  void logZoneAccessEvent(ZoneAccessResult result, String macAddress);
}
