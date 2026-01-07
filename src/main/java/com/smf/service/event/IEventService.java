package com.smf.service.event;

import com.smf.dto.device.DeviceEventRequest;
import com.smf.model.Event;
import java.util.List;

public interface IEventService {
  List<Event> getEvents(int since);

  List<Event> getAllEvents();

  void processEvent(DeviceEventRequest req);
}
