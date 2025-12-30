package com.smf.controller;

import com.smf.dto.api.ApiResponse;
import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.dto.device.DeviceResponse;
import com.smf.dto.device.DeviceTestRequest;
import com.smf.service.device.IDeviceService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/devices")
public class DeviceController {

  private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

  private final IDeviceService deviceService;

  public DeviceController(IDeviceService deviceService) {
    this.deviceService = deviceService;
  }

  @PostMapping("/test")
  public ResponseEntity<ApiResponse> testDevice(@RequestBody @Validated DeviceTestRequest request) {
    logger.info("Received test payload from device: {}", request);
    return ResponseEntity.ok(new ApiResponse(true, "Payload received successfully", request));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/")
  public ResponseEntity<ApiResponse> registerDevice(
      @Valid @RequestBody DeviceRegisterRequest request) {
    DeviceResponse response = deviceService.registerDevice(request);
    return ResponseEntity.ok(new ApiResponse(true, "Device registered successfully", response));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/{deviceId}")
  public ResponseEntity<ApiResponse> getDevice(@PathVariable UUID deviceId) {
    DeviceResponse response = deviceService.getDeviceById(deviceId);
    return ResponseEntity.ok(new ApiResponse(true, "Device fetched successfully", response));
  }
}
