package com.smf.service.zone;

import com.smf.dto.zone.ZoneRequest;
import com.smf.dto.zone.ZoneResponse;
import com.smf.model.Device;
import com.smf.model.Role;
import com.smf.model.Zone;
import com.smf.repo.DeviceRepository;
import com.smf.repo.RoleRepository;
import com.smf.repo.ZoneRepository;
import com.smf.util.AppError;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZoneService implements IZoneService {

  private final ZoneRepository zoneRepository;
  private final RoleRepository roleRepository;
  private final DeviceRepository deviceRepository;

  @Override
  public ZoneResponse createZone(ZoneRequest request) {
    if (zoneRepository.findByName(request.name()).isPresent()) {
      throw new AppError(HttpStatus.CONFLICT, "Zone with this name already exists");
    }

    Zone zone = new Zone();
    zone.setName(request.name());
    zone = zoneRepository.save(zone);
    return mapToZoneResponse(zone);
  }

  @Override
  public ZoneResponse getZoneById(UUID id) {
    Zone zone =
        zoneRepository
            .findById(id)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Zone not found"));
    return mapToZoneResponse(zone);
  }

  @Override
  public List<ZoneResponse> getAllZones() {
    return zoneRepository.findAll().stream()
        .map(this::mapToZoneResponse)
        .collect(Collectors.toList());
  }

  @Override
  public List<ZoneResponse> searchByName(String name) {
    return zoneRepository.findByNameContainingIgnoreCase(name).stream()
        .map(this::mapToZoneResponse)
        .collect(Collectors.toList());
  }

  @Override
  public ZoneResponse updateZone(UUID id, ZoneRequest request) {
    Zone zone =
        zoneRepository
            .findById(id)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Zone not found"));

    zoneRepository
        .findByName(request.name())
        .ifPresent(
            existing -> {
              if (!existing.getId().equals(id)) {
                throw new AppError(HttpStatus.CONFLICT, "Zone with this name already exists");
              }
            });

    zone.setName(request.name());
    zone = zoneRepository.save(zone);
    return mapToZoneResponse(zone);
  }

  @Override
  public void deleteZone(UUID id) {
    Zone zone =
        zoneRepository
            .findById(id)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Zone not found"));
    zoneRepository.delete(zone);
  }

  private ZoneResponse mapToZoneResponse(Zone zone) {
    return new ZoneResponse(zone.getId(), zone.getName());
  }

  @Override
  public boolean canDeviceAccessZone(UUID deviceId, UUID zoneId) {
    Device device =
        deviceRepository
            .findById(deviceId)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Device not found"));

    Zone zone =
        zoneRepository
            .findById(zoneId)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Zone not found"));

    if (zone.getAllowedRoles().isEmpty()) {
      return true;
    }

    Set<String> ownerRoleNames =
        device.getOwner().getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());

    Set<String> zoneRoleNames =
        zone.getAllowedRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());

    for (String role : ownerRoleNames) {
      if (zoneRoleNames.contains(role)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<ZoneResponse> getAccessibleZones(UUID deviceId) {
    Device device =
        deviceRepository
            .findById(deviceId)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Device not found"));

    Set<String> ownerRoleNames =
        device.getOwner().getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());

    return zoneRepository.findAll().stream()
        .filter(
            zone -> {
              if (zone.getAllowedRoles().isEmpty()) {
                return true;
              }
              Set<String> zoneRoleNames =
                  zone.getAllowedRoles().stream()
                      .map(Role::getRoleName)
                      .collect(Collectors.toSet());
              for (String role : ownerRoleNames) {
                if (zoneRoleNames.contains(role)) {
                  return true;
                }
              }
              return false;
            })
        .map(this::mapToZoneResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void assignRoleToZone(UUID zoneId, Long roleId) {
    Zone zone =
        zoneRepository
            .findById(zoneId)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Zone not found"));

    Role role =
        roleRepository
            .findById(roleId)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Role not found"));

    zone.getAllowedRoles().add(role);
    zoneRepository.save(zone);
  }

  @Override
  @Transactional
  public void removeRoleFromZone(UUID zoneId, Long roleId) {
    Zone zone =
        zoneRepository
            .findById(zoneId)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Zone not found"));

    Role role =
        roleRepository
            .findById(roleId)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Role not found"));

    zone.getAllowedRoles().remove(role);
    zoneRepository.save(zone);
  }

  @Override
  public Zone findZoneById(UUID zoneId) {
    return zoneRepository
        .findById(zoneId)
        .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Zone not found"));
  }
}
