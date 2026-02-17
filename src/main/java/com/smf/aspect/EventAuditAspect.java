package com.smf.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smf.dto.api.ApiResponse;
import com.smf.dto.zone.ZoneAccessResult;
import com.smf.dto.zone.ZoneEntryRequest;
import com.smf.model.Event;
import com.smf.model.enums.EventTypes;
import com.smf.repo.EventRepository;
import com.smf.util.LogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class EventAuditAspect {
  private final EventRepository eventRepo;
  private final ObjectMapper objectMapper;

  @AfterReturning(pointcut = "@annotation(logEvent)", returning = "returnValue")
  public void logEvent(JoinPoint joinPoint, LogEvent logEvent, Object returnValue) {
    Object[] args = joinPoint.getArgs();
    String macAddress = (String) args[0];

    boolean isZoneEntry = false;
    for (Object arg : args) {
      if (arg instanceof ZoneEntryRequest) {
        isZoneEntry = true;
        break;
      }
    }

    if (isZoneEntry) {
      ResponseEntity<ApiResponse> responseEntity = (ResponseEntity<ApiResponse>) returnValue;
      handleZoneEntryEvent(macAddress, responseEntity.getBody());
    } else {
      eventRepo.save(new Event(logEvent.eventType(), macAddress, "{}"));
    }
  }

  private void handleZoneEntryEvent(String macAddress, ApiResponse response) {
    ZoneAccessResult result = (ZoneAccessResult) response.data();

    ObjectNode metadata = objectMapper.createObjectNode();
    metadata.put("zoneId", result.zoneId().toString());
    metadata.put("zoneName", result.zoneName());
    metadata.put("accessGranted", result.granted());
    metadata.put("message", result.message());
    metadata.set("userRoles", objectMapper.valueToTree(result.userRoles()));
    metadata.set("zoneAllowedRoles", objectMapper.valueToTree(result.zoneAllowedRoles()));

    EventTypes eventType = result.granted() ? EventTypes.ACCESS_GRANTED : EventTypes.ACCESS_DENIED;
    eventRepo.save(new Event(eventType, macAddress, metadata.toString()));
  }
}
