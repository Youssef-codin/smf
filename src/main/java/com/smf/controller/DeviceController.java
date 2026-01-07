package com.smf.controller;

import com.smf.dto.api.ApiResponse;
import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.dto.device.DeviceResponse;
import com.smf.service.device.IDeviceService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/devices")
@RequiredArgsConstructor
public class DeviceController {

  private final IDeviceService deviceService;

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
