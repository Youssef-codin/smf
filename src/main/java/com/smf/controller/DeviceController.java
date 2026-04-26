package com.smf.controller;

import com.smf.dto.api.ApiResponse;
import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.dto.device.DeviceResponse;
import com.smf.dto.zone.ZoneAccessResult;
import com.smf.dto.zone.ZoneEntryRequest;
import com.smf.security.RateLimit;
import com.smf.security.RateLimitKeyType;
import com.smf.service.device.IDeviceService;
import com.smf.service.zone.IZoneService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/devices")
@RequiredArgsConstructor
public class DeviceController {

  private final IDeviceService deviceService;
  private final IZoneService zoneService;

  @RateLimit(limit = 50, duration = 60, keyType = RateLimitKeyType.USER)
  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/")
  public ResponseEntity<ApiResponse> registerDevice(
      @Valid @RequestBody DeviceRegisterRequest request) {
    DeviceResponse response = deviceService.registerDevice(request);
    return ResponseEntity.ok(new ApiResponse(true, "Device registered successfully", response));
  }

  @RateLimit(limit = 100, duration = 60, keyType = RateLimitKeyType.USER)
  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/")
  public ResponseEntity<ApiResponse> getAllDevices() {

    List<DeviceResponse> devices = deviceService.getAllDevices();
    return ResponseEntity.ok(new ApiResponse(true, "Devices fetched successfully", devices));
  }

  @RateLimit(limit = 100, duration = 60, keyType = RateLimitKeyType.USER)
  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse> getDeviceById(@PathVariable UUID id) {

    DeviceResponse device = deviceService.getDeviceById(id);
    return ResponseEntity.ok(new ApiResponse(true, "Device fetched successfully", device));
  }

  @RateLimit(limit = 50, duration = 60, keyType = RateLimitKeyType.USER)
  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse> updateDevice(
      @PathVariable UUID id, @Valid @RequestBody DeviceRegisterRequest request) {

    DeviceResponse updated = deviceService.updateDevice(id, request);
    return ResponseEntity.ok(new ApiResponse(true, "Device updated successfully", updated));
  }

  @RateLimit(limit = 20, duration = 60, keyType = RateLimitKeyType.USER)
  @PreAuthorize("hasAuthority('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse> deleteDevice(@PathVariable UUID id) {

    deviceService.deleteDevice(id);
    return ResponseEntity.ok(new ApiResponse(true, "Device deleted successfully", null));
  }

  @RateLimit(limit = 30, duration = 60, keyType = RateLimitKeyType.DEVICE)
  @PreAuthorize("hasAuthority('DEVICE')")
  @PostMapping("/zone-entry")
  public ResponseEntity<ApiResponse> handleZoneEntry(
      @AuthenticationPrincipal String macAddress, @Valid @RequestBody ZoneEntryRequest request) {

    ZoneAccessResult result = zoneService.checkZoneAccess(macAddress, request);

    return ResponseEntity.ok(
        new ApiResponse(
            result.granted(), result.granted() ? "Access granted" : "Access denied", result));
  }
}
