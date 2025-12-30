package com.smf.service.event;

import com.smf.model.Event;
import java.util.List;

public interface IEventService {
  List<Event> getEvents(int since);

  List<Event> getAllEvents();
}
