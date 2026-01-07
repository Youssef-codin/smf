package com.smf.controller;

import com.smf.dto.api.ApiResponse;
import com.smf.dto.device.DeviceEventRequest;
import com.smf.model.Event;
import com.smf.service.event.IEventService;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/events")
public class EventController {
  private final IEventService service;

  @PreAuthorize("hasAuthority('USER')")
  @GetMapping("/client")
  public ResponseEntity<ApiResponse> getEvents(
      @RequestParam(required = false) @Min(1) Integer since) {

    List<Event> events = since != null ? service.getEvents(since) : service.getAllEvents();

    return ResponseEntity.ok(new ApiResponse(true, "Events fetched successfully", events));
  }

  @PreAuthorize("hasAuthority('USER')")
  @PostMapping("/device")
  public ResponseEntity<ApiResponse> event(@RequestBody @Validated DeviceEventRequest request) {
    service.processEvent(request);
    return ResponseEntity.ok(new ApiResponse(true, "Event completed", null));
  }
}
