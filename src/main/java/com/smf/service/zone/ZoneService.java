package com.smf.service.zone;

import com.smf.dto.zone.ZoneRequest;
import com.smf.dto.zone.ZoneResponse;
import com.smf.model.Zone;
import com.smf.repo.ZoneRepository;
import com.smf.util.AppError;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZoneService implements IZoneService {

  private final ZoneRepository zoneRepository;

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
}
