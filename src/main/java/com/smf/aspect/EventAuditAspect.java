package com.smf.aspect;

import com.smf.dto.device.DeviceEventRequest;
import com.smf.model.Event;
import com.smf.repo.EventRepository;
import com.smf.util.LogEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class EventAuditAspect {
  private final EventRepository eventRepo;

  @AfterReturning("@annotation(event)")
  public void logEvent(JoinPoint joinPoint, LogEvent event) {
    Object[] args = joinPoint.getArgs();
    String id = extractDeviceId(args);

    if (id != null) {
      eventRepo.save(new Event(event.eventType(), id, "{}"));
    }
  }

  private String extractDeviceId(Object[] args) {
    for (Object arg : args) {
      if (arg instanceof String) return (String) arg;
      if (arg instanceof DeviceEventRequest) return ((DeviceEventRequest) arg).macAddress();
    }
    return null;
  }
}
