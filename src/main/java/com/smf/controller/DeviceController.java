package com.smf.controller;

import com.smf.dto.api.ApiResponse;
import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.dto.device.DeviceResponse;
import com.smf.dto.zone.ZoneAccessResult;
import com.smf.dto.zone.ZoneEntryRequest;
import com.smf.model.Device;
import com.smf.model.Zone;
import com.smf.model.enums.EventTypes;
import com.smf.repo.DeviceRepository;
import com.smf.service.device.IDeviceService;
import com.smf.service.zone.IZoneService;
import com.smf.util.AppError;
import com.smf.util.LogEvent;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/devices")
@RequiredArgsConstructor
public class DeviceController {

  private final IDeviceService deviceService;
  private final IZoneService zoneService;
  private final DeviceRepository deviceRepository;

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

  @PostMapping("/{macAddress}/zone-entry")
  @LogEvent(eventType = EventTypes.ACCESS_GRANTED)
  public ResponseEntity<ApiResponse> handleZoneEntry(
      @PathVariable String macAddress, @Valid @RequestBody ZoneEntryRequest request) {

    Device device =
        deviceRepository
            .findByMacAddress(macAddress)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Device not found"));

    boolean canAccess = zoneService.canDeviceAccessZone(device.getId(), request.zoneId());

    Zone zone = zoneService.findZoneById(request.zoneId());
    Set<String> userRoles =
        device.getOwner().getRoles().stream()
            .map(role -> role.getRoleName())
            .collect(Collectors.toSet());
    Set<String> zoneAllowedRoles =
        zone.getAllowedRoles().stream()
            .map(role -> role.getRoleName())
            .collect(Collectors.toSet());

    ZoneAccessResult result =
        new ZoneAccessResult(
            canAccess,
            request.zoneId(),
            zone.getName(),
            userRoles,
            zoneAllowedRoles,
            canAccess
                ? "Access granted"
                : "Access denied - insufficient role permissions");

    return ResponseEntity.ok(
        new ApiResponse(canAccess, canAccess ? "Access granted" : "Access denied", result));
  }
}
