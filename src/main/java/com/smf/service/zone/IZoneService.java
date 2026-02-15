package com.smf.service.zone;

import com.smf.dto.zone.ZoneRequest;
import com.smf.dto.zone.ZoneResponse;
import java.util.List;
import java.util.UUID;

public interface IZoneService {
  ZoneResponse createZone(ZoneRequest request);

  ZoneResponse getZoneById(UUID id);

  List<ZoneResponse> searchByName(String name);

  List<ZoneResponse> getAllZones();

  ZoneResponse updateZone(UUID id, ZoneRequest request);

  void deleteZone(UUID id);
}
