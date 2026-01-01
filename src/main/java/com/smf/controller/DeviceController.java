package com.smf.controller;

import com.smf.dto.api.ApiResponse;
import com.smf.dto.device.DeviceOfflineRequest;
import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.dto.device.DeviceResponse;
import com.smf.dto.device.DeviceSosRequest;
import com.smf.dto.device.DeviceTestRequest;
import com.smf.model.enums.EventTypes;
import com.smf.service.device.IDeviceService;
import com.smf.util.LogEvent;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/devices")
public class DeviceController {

  private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

  private final IDeviceService deviceService;

  public DeviceController(IDeviceService deviceService) {
    this.deviceService = deviceService;
  }

  @LogEvent(eventType = EventTypes.TESTING)
  @PostMapping("/test")
  public ResponseEntity<ApiResponse> testDevice(@RequestBody @Validated DeviceTestRequest request) {

    logger.info("Received test payload from device: {}", request);
    return ResponseEntity.ok(new ApiResponse(true, "Payload received successfully", request));
  }

  @LogEvent(eventType = EventTypes.SOS_TRIGGERED)
  @PreAuthorize("hasAuthority('USER')")
  @PostMapping("/action/sos")
  public ResponseEntity<ApiResponse> sendSos(@RequestBody @Validated DeviceSosRequest request) {

    DeviceResponse response = deviceService.handleSos(request.getMacAddress());
    return ResponseEntity.ok(new ApiResponse(true, "SOS received", response));
  }

  @LogEvent(eventType = EventTypes.DEVICE_OFFLINE)
  @PreAuthorize("hasAuthority('USER')")
  @PostMapping("/action/offline")
  public ResponseEntity<ApiResponse> deviceOffline(
      @RequestBody @Validated DeviceOfflineRequest request) {

    DeviceResponse response = deviceService.handleSos(request.MacAddress());
    return ResponseEntity.ok(new ApiResponse(true, "SOS received", response));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/")
  public ResponseEntity<ApiResponse> registerDevice(
      @Valid @RequestBody DeviceRegisterRequest request) {

    DeviceResponse response = deviceService.registerDevice(request);
    return ResponseEntity.ok(new ApiResponse(true, "Device registered successfully", response));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/")
  public ResponseEntity<ApiResponse> getAllDevices() {

    List<DeviceResponse> devices = deviceService.getAllDevices();
    return ResponseEntity.ok(new ApiResponse(true, "Devices fetched successfully", devices));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse> getDeviceById(@PathVariable UUID id) {

    DeviceResponse device = deviceService.getDeviceById(id);
    return ResponseEntity.ok(new ApiResponse(true, "Device fetched successfully", device));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse> updateDevice(
      @PathVariable UUID id, @Valid @RequestBody DeviceRegisterRequest request) {

    DeviceResponse updated = deviceService.updateDevice(id, request);
    return ResponseEntity.ok(new ApiResponse(true, "Device updated successfully", updated));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse> deleteDevice(@PathVariable UUID id) {

    deviceService.deleteDevice(id);
    return ResponseEntity.ok(new ApiResponse(true, "Device deleted successfully", null));
  }
}
