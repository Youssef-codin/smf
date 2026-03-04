package com.smf.service.zone;

import com.smf.dto.zone.ZoneRequest;
import com.smf.dto.zone.ZoneResponse;
import com.smf.model.Role;
import com.smf.model.Zone;
import com.smf.util.AppError;
import com.smf.repo.DeviceRepository;
import com.smf.repo.RoleRepository;
import com.smf.repo.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ZoneServiceTest {

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private ZoneService zoneService;

    private Zone testZone;
    private UUID zoneId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        zoneId = UUID.randomUUID();
        testZone = new Zone();
        testZone.setId(zoneId);
        testZone.setName("Test Zone");
        testZone.setAllowedRoles(new HashSet<>());
    }

    @Test
    void createZone_success() {
        ZoneRequest request = new ZoneRequest("Test Zone");

        when(zoneRepository.findByName("Test Zone")).thenReturn(Optional.empty());
        when(zoneRepository.save(any(Zone.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ZoneResponse response = zoneService.createZone(request);

        assertNotNull(response);
        assertEquals("Test Zone", response.name());
    }

    @Test
    void createZone_duplicateName() {
        ZoneRequest request = new ZoneRequest("Test Zone");

        when(zoneRepository.findByName("Test Zone")).thenReturn(Optional.of(testZone));

        AppError exception =
                assertThrows(AppError.class, () -> zoneService.createZone(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Zone with this name already exists", exception.getMessage());
    }

    @Test
    void getZoneById_success() {
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(testZone));

        ZoneResponse response = zoneService.getZoneById(zoneId);

        assertNotNull(response);
        assertEquals("Test Zone", response.name());
        assertEquals(zoneId, response.id());
    }

    @Test
    void getZoneById_notFound() {
        UUID id = UUID.randomUUID();
        when(zoneRepository.findById(id)).thenReturn(Optional.empty());

        AppError exception =
                assertThrows(AppError.class, () -> zoneService.getZoneById(id));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Zone not found", exception.getMessage());
    }

    @Test
    void getAllZones_success() {
        when(zoneRepository.findAll()).thenReturn(List.of(testZone));

        List<ZoneResponse> zones = zoneService.getAllZones();

        assertEquals(1, zones.size());
        assertEquals("Test Zone", zones.get(0).name());
    }

    @Test
    void updateZone_success() {
        ZoneRequest request = new ZoneRequest("Updated Zone");

        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(testZone));
        when(zoneRepository.findByName("Updated Zone")).thenReturn(Optional.empty());
        when(zoneRepository.save(any(Zone.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ZoneResponse response = zoneService.updateZone(zoneId, request);

        assertEquals("Updated Zone", response.name());
        assertEquals(zoneId, response.id());
    }

    @Test
    void updateZone_conflict() {
        ZoneRequest request = new ZoneRequest("Conflict Zone");
        Zone otherZone = new Zone();
        otherZone.setId(UUID.randomUUID());
        otherZone.setName("Conflict Zone");

        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(testZone));
        when(zoneRepository.findByName("Conflict Zone")).thenReturn(Optional.of(otherZone));

        AppError exception =
                assertThrows(AppError.class, () -> zoneService.updateZone(zoneId, request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Zone with this name already exists", exception.getMessage());
    }

    @Test
    void deleteZone_success() {
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(testZone));
        doNothing().when(zoneRepository).delete(testZone);

        assertDoesNotThrow(() -> zoneService.deleteZone(zoneId));
        verify(zoneRepository, times(1)).delete(testZone);
    }

    @Test
    void deleteZone_notFound() {
        UUID id = UUID.randomUUID();
        when(zoneRepository.findById(id)).thenReturn(Optional.empty());

        AppError exception =
                assertThrows(AppError.class, () -> zoneService.deleteZone(id));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Zone not found", exception.getMessage());
    }
}