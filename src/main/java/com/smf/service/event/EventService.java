package com.smf.service.event;

import com.smf.model.Event;
import com.smf.repo.EventRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EventService implements IEventService {

  private final EventRepository eventRepo;

  public EventService(EventRepository repo) {
    this.eventRepo = repo;
  }

  @Override
  public List<Event> getEvents(int since) {
    Instant sinceTime = Instant.now().minusSeconds(since);
    return eventRepo.findRecent(sinceTime);
  }

  @Override
  public List<Event> getAllEvents() {
    return eventRepo.findAll();
  }
}
