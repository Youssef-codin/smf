package com.smf.service.event;

import com.smf.model.Event;
import com.smf.model.enums.EventTypes;
import com.smf.repo.EventRepository;
import com.smf.util.LogEvent;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EventService implements IEventService {

  private final EventRepository eventRepo;

  @Override
  public List<Event> getEvents(int since) {
    Instant sinceTime = Instant.now().minusSeconds(since);
    return eventRepo.findRecent(sinceTime);
  }

  @Override
  public List<Event> getAllEvents() {
    return eventRepo.findAll();
  }

  // NOTE: Could add logic later if needed
  @Override
  @LogEvent(eventType = EventTypes.TESTING)
  public void handleTest(String macAddress) {}

  @Override
  @LogEvent(eventType = EventTypes.ACCESS_DENIED)
  public void handleDenied(String macAddress) {}

  @Override
  @LogEvent(eventType = EventTypes.DEVICE_ONLINE)
  public void handleOnline(String macAddress) {}

  @Override
  @LogEvent(eventType = EventTypes.ACCESS_GRANTED)
  public void handleGranted(String macAddress) {}

  @Override
  @LogEvent(eventType = EventTypes.SOS_TRIGGERED)
  public void handleSos(String macAddress) {}

  @Override
  @LogEvent(eventType = EventTypes.DEVICE_OFFLINE)
  public void handleOffline(String macAddress) {}
}

