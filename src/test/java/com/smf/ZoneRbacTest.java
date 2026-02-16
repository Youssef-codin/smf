package com.smf;

import com.smf.dto.zone.ZoneRequest;
import com.smf.dto.zone.ZoneResponse;
import com.smf.model.Device;
import com.smf.model.Role;
import com.smf.model.User;
import com.smf.model.Zone;
import com.smf.repo.DeviceRepository;
import com.smf.repo.RoleRepository;
import com.smf.repo.ZoneRepository;
import com.smf.service.zone.ZoneService;
import com.smf.util.AppError;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZoneRbacTest {

  @Mock private ZoneRepository zoneRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private DeviceRepository deviceRepository;

  @InjectMocks private ZoneService zoneService;

  private Role engineerRole;
  private Role workerRole;
  private Role managerRole;
  private User engineerUser;
  private User workerUser;
  private User multiRoleUser;
  private User noRoleUser;
  private User adminUser;
  private Device engineerDevice;
  private Device workerDevice;
  private Device multiRoleDevice;
  private Device noRoleDevice;
  private Device adminDevice;
  private Zone restrictedZone;
  private Zone openZone;
  private Zone multiRoleZone;

  @BeforeEach
  void setUp() {
    engineerRole = createRole(1L, "ENGINEER", false);
    workerRole = createRole(2L, "WORKER", false);
    managerRole = createRole(3L, "MANAGER", false);

    engineerUser = createUser(UUID.randomUUID(), "engineer@test.com", Set.of(engineerRole));
    workerUser = createUser(UUID.randomUUID(), "worker@test.com", Set.of(workerRole));
    multiRoleUser = createUser(UUID.randomUUID(), "multi@test.com", Set.of(engineerRole, workerRole));
    noRoleUser = createUser(UUID.randomUUID(), "norole@test.com", new HashSet<>());
    adminUser = createUser(UUID.randomUUID(), "admin@test.com", Set.of(engineerRole));
    adminUser.getRoles().iterator().next().setAdmin(true);

    engineerDevice = createDevice(UUID.randomUUID(), "AA:BB:CC:DD:EE:01", engineerUser);
    workerDevice = createDevice(UUID.randomUUID(), "AA:BB:CC:DD:EE:02", workerUser);
    multiRoleDevice = createDevice(UUID.randomUUID(), "AA:BB:CC:DD:EE:03", multiRoleUser);
    noRoleDevice = createDevice(UUID.randomUUID(), "AA:BB:CC:DD:EE:04", noRoleUser);
    adminDevice = createDevice(UUID.randomUUID(), "AA:BB:CC:DD:EE:05", adminUser);

    restrictedZone = createZone(UUID.randomUUID(), "Restricted Zone", new HashSet<>(Set.of(engineerRole)));
    openZone = createZone(UUID.randomUUID(), "Open Zone", new HashSet<>());
    multiRoleZone = createZone(UUID.randomUUID(), "Multi Role Zone", new HashSet<>(Set.of(engineerRole, managerRole)));
  }

  private Role createRole(Long id, String name, boolean isAdmin) {
    Role role = new Role();
    role.setId(id);
    role.setRoleName(name);
    role.setAdmin(isAdmin);
    return role;
  }

  private User createUser(UUID id, String email, Set<Role> roles) {
    User user = new User();
    user.setId(id);
    user.setEmail(email);
    user.setUsername(email.split("@")[0]);
    user.setRoles(roles);
    return user;
  }

  private Device createDevice(UUID id, String macAddress, User owner) {
    Device device = new Device();
    device.setId(id);
    device.setMacAddress(macAddress);
    device.setOwner(owner);
    device.setLastLocationLat(0.0);
    device.setLastLocationLon(0.0);
    return device;
  }

  private Zone createZone(UUID id, String name, Set<Role> allowedRoles) {
    Zone zone = new Zone();
    zone.setId(id);
    zone.setName(name);
    zone.setAllowedRoles(allowedRoles);
    return zone;
  }

  @Test
  void engineerCanAccessRestrictedZone() {
    when(deviceRepository.findById(engineerDevice.getId())).thenReturn(Optional.of(engineerDevice));
    when(zoneRepository.findById(restrictedZone.getId())).thenReturn(Optional.of(restrictedZone));

    boolean canAccess = zoneService.canDeviceAccessZone(engineerDevice.getId(), restrictedZone.getId());

    assertTrue(canAccess, "Engineer should be able to access restricted zone");
  }

  @Test
  void workerCannotAccessRestrictedZone() {
    when(deviceRepository.findById(workerDevice.getId())).thenReturn(Optional.of(workerDevice));
    when(zoneRepository.findById(restrictedZone.getId())).thenReturn(Optional.of(restrictedZone));

    boolean canAccess = zoneService.canDeviceAccessZone(workerDevice.getId(), restrictedZone.getId());

    assertFalse(canAccess, "Worker should not be able to access restricted zone");
  }

  @Test
  void everyoneCanAccessOpenZone() {
    when(deviceRepository.findById(engineerDevice.getId())).thenReturn(Optional.of(engineerDevice));
    when(deviceRepository.findById(workerDevice.getId())).thenReturn(Optional.of(workerDevice));
    when(zoneRepository.findById(openZone.getId())).thenReturn(Optional.of(openZone));

    boolean engineerCanAccess = zoneService.canDeviceAccessZone(engineerDevice.getId(), openZone.getId());
    boolean workerCanAccess = zoneService.canDeviceAccessZone(workerDevice.getId(), openZone.getId());

    assertTrue(engineerCanAccess, "Engineer should access open zone");
    assertTrue(workerCanAccess, "Worker should access open zone");
  }

  @Test
  void getAccessibleZonesReturnsCorrectZones() {
    when(deviceRepository.findById(engineerDevice.getId())).thenReturn(Optional.of(engineerDevice));
    when(deviceRepository.findById(workerDevice.getId())).thenReturn(Optional.of(workerDevice));
    when(zoneRepository.findAll()).thenReturn(List.of(restrictedZone, openZone));

    List<ZoneResponse> engineerZones = zoneService.getAccessibleZones(engineerDevice.getId());
    List<ZoneResponse> workerZones = zoneService.getAccessibleZones(workerDevice.getId());

    assertEquals(2, engineerZones.size(), "Engineer should see both zones");
    assertEquals(1, workerZones.size(), "Worker should only see open zone");
  }

  @Test
  void assignAndRemoveRoleFromZone() {
    Zone zone = createZone(UUID.randomUUID(), "Test Zone", new HashSet<>());
    when(zoneRepository.findById(zone.getId())).thenReturn(Optional.of(zone));
    when(roleRepository.findById(workerRole.getId())).thenReturn(Optional.of(workerRole));
    when(zoneRepository.save(any(Zone.class))).thenReturn(zone);

    zoneService.assignRoleToZone(zone.getId(), workerRole.getId());

    assertTrue(zone.getAllowedRoles().contains(workerRole), "Worker role should be assigned to zone");

    when(deviceRepository.findById(workerDevice.getId())).thenReturn(Optional.of(workerDevice));

    boolean canAccessBefore = zoneService.canDeviceAccessZone(workerDevice.getId(), zone.getId());
    assertTrue(canAccessBefore, "Worker should access zone after role assignment");

    zoneService.removeRoleFromZone(zone.getId(), workerRole.getId());

    assertFalse(zone.getAllowedRoles().contains(workerRole), "Worker role should be removed from zone");

    boolean canAccessAfter = zoneService.canDeviceAccessZone(workerDevice.getId(), zone.getId());
    assertTrue(canAccessAfter, "Worker should still access zone with no restrictions");
  }

  @Test
  void userWithMultipleRoles_oneAllowed_shouldGrantAccess() {
    when(deviceRepository.findById(multiRoleDevice.getId())).thenReturn(Optional.of(multiRoleDevice));
    when(zoneRepository.findById(restrictedZone.getId())).thenReturn(Optional.of(restrictedZone));

    boolean canAccess = zoneService.canDeviceAccessZone(multiRoleDevice.getId(), restrictedZone.getId());

    assertTrue(canAccess, "User with ENGINEER role should access zone that allows ENGINEER");
  }

  @Test
  void zoneWithMultipleRoles_userHasNone_shouldDenyAccess() {
    when(deviceRepository.findById(workerDevice.getId())).thenReturn(Optional.of(workerDevice));
    when(zoneRepository.findById(multiRoleZone.getId())).thenReturn(Optional.of(multiRoleZone));

    boolean canAccess = zoneService.canDeviceAccessZone(workerDevice.getId(), multiRoleZone.getId());

    assertFalse(canAccess, "Worker should not access zone that only allows ENGINEER and MANAGER");
  }

  @Test
  void deviceNotFound_shouldThrowException() {
    UUID nonExistentDeviceId = UUID.randomUUID();
    when(deviceRepository.findById(nonExistentDeviceId)).thenReturn(Optional.empty());

    AppError exception = assertThrows(AppError.class, () ->
        zoneService.canDeviceAccessZone(nonExistentDeviceId, restrictedZone.getId())
    );

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("Device not found", exception.getMessage());
  }

  @Test
  void zoneNotFound_shouldThrowException() {
    UUID nonExistentZoneId = UUID.randomUUID();
    when(deviceRepository.findById(engineerDevice.getId())).thenReturn(Optional.of(engineerDevice));
    when(zoneRepository.findById(nonExistentZoneId)).thenReturn(Optional.empty());

    AppError exception = assertThrows(AppError.class, () ->
        zoneService.canDeviceAccessZone(engineerDevice.getId(), nonExistentZoneId)
    );

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("Zone not found", exception.getMessage());
  }

  @Test
  void userWithNoRoles_shouldDenyAccess() {
    when(deviceRepository.findById(noRoleDevice.getId())).thenReturn(Optional.of(noRoleDevice));
    when(zoneRepository.findById(restrictedZone.getId())).thenReturn(Optional.of(restrictedZone));

    boolean canAccess = zoneService.canDeviceAccessZone(noRoleDevice.getId(), restrictedZone.getId());

    assertFalse(canAccess, "User with no roles should not access restricted zone");
  }

  @Test
  void adminUser_shouldFollowNormalRules() {
    when(deviceRepository.findById(adminDevice.getId())).thenReturn(Optional.of(adminDevice));
    when(zoneRepository.findById(restrictedZone.getId())).thenReturn(Optional.of(restrictedZone));

    boolean canAccess = zoneService.canDeviceAccessZone(adminDevice.getId(), restrictedZone.getId());

    assertTrue(canAccess, "Admin with ENGINEER role should access zone that allows ENGINEER");
  }

  @Test
  void assignDuplicateRole_shouldBeIdempotent() {
    when(zoneRepository.findById(restrictedZone.getId())).thenReturn(Optional.of(restrictedZone));
    when(roleRepository.findById(engineerRole.getId())).thenReturn(Optional.of(engineerRole));
    when(zoneRepository.save(any(Zone.class))).thenReturn(restrictedZone);

    int initialSize = restrictedZone.getAllowedRoles().size();

    zoneService.assignRoleToZone(restrictedZone.getId(), engineerRole.getId());

    assertEquals(initialSize, restrictedZone.getAllowedRoles().size(), "Duplicate assignment should not add role again");
  }

  @Test
  void removeNonExistentRole_shouldBeIdempotent() {
    Zone zone = createZone(UUID.randomUUID(), "Test Zone", new HashSet<>());
    when(zoneRepository.findById(zone.getId())).thenReturn(Optional.of(zone));
    when(roleRepository.findById(workerRole.getId())).thenReturn(Optional.of(workerRole));
    when(zoneRepository.save(any(Zone.class))).thenReturn(zone);

    assertDoesNotThrow(() -> zoneService.removeRoleFromZone(zone.getId(), workerRole.getId()));
    assertTrue(zone.getAllowedRoles().isEmpty(), "Zone should still have no roles");
  }

  @Test
  void createZone_success() {
    ZoneRequest request = new ZoneRequest("New Zone");
    when(zoneRepository.findByName("New Zone")).thenReturn(Optional.empty());
    when(zoneRepository.save(any(Zone.class))).thenAnswer(invocation -> {
      Zone zone = invocation.getArgument(0);
      zone.setId(UUID.randomUUID());
      return zone;
    });

    ZoneResponse response = zoneService.createZone(request);

    assertNotNull(response);
    assertEquals("New Zone", response.name());
  }

  @Test
  void createZone_duplicateName_shouldThrowException() {
    ZoneRequest request = new ZoneRequest("Existing Zone");
    when(zoneRepository.findByName("Existing Zone")).thenReturn(Optional.of(restrictedZone));

    AppError exception = assertThrows(AppError.class, () -> zoneService.createZone(request));

    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    assertEquals("Zone with this name already exists", exception.getMessage());
  }

  @Test
  void getZoneById_success() {
    when(zoneRepository.findById(restrictedZone.getId())).thenReturn(Optional.of(restrictedZone));

    ZoneResponse response = zoneService.getZoneById(restrictedZone.getId());

    assertNotNull(response);
    assertEquals(restrictedZone.getId(), response.id());
    assertEquals(restrictedZone.getName(), response.name());
  }

  @Test
  void getZoneById_notFound_shouldThrowException() {
    UUID nonExistentId = UUID.randomUUID();
    when(zoneRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    AppError exception = assertThrows(AppError.class, () -> zoneService.getZoneById(nonExistentId));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("Zone not found", exception.getMessage());
  }

  @Test
  void getAllZones_success() {
    when(zoneRepository.findAll()).thenReturn(List.of(restrictedZone, openZone));

    List<ZoneResponse> zones = zoneService.getAllZones();

    assertEquals(2, zones.size());
  }

  @Test
  void searchByName_success() {
    when(zoneRepository.findByNameContainingIgnoreCase("Restrict")).thenReturn(List.of(restrictedZone));

    List<ZoneResponse> zones = zoneService.searchByName("Restrict");

    assertEquals(1, zones.size());
    assertEquals("Restricted Zone", zones.get(0).name());
  }

  @Test
  void updateZone_success() {
    UUID zoneId = restrictedZone.getId();
    ZoneRequest request = new ZoneRequest("Updated Zone Name");
    when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(restrictedZone));
    when(zoneRepository.findByName("Updated Zone Name")).thenReturn(Optional.empty());
    when(zoneRepository.save(any(Zone.class))).thenReturn(restrictedZone);

    ZoneResponse response = zoneService.updateZone(zoneId, request);

    assertNotNull(response);
    assertEquals("Updated Zone Name", restrictedZone.getName());
  }

  @Test
  void updateZone_notFound_shouldThrowException() {
    UUID nonExistentId = UUID.randomUUID();
    ZoneRequest request = new ZoneRequest("New Name");
    when(zoneRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    AppError exception = assertThrows(AppError.class, () -> zoneService.updateZone(nonExistentId, request));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("Zone not found", exception.getMessage());
  }

  @Test
  void updateZone_duplicateName_shouldThrowException() {
    UUID zoneId = restrictedZone.getId();
    ZoneRequest request = new ZoneRequest("Open Zone");
    when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(restrictedZone));
    when(zoneRepository.findByName("Open Zone")).thenReturn(Optional.of(openZone));

    AppError exception = assertThrows(AppError.class, () -> zoneService.updateZone(zoneId, request));

    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    assertEquals("Zone with this name already exists", exception.getMessage());
  }

  @Test
  void deleteZone_success() {
    when(zoneRepository.findById(restrictedZone.getId())).thenReturn(Optional.of(restrictedZone));
    doNothing().when(zoneRepository).delete(restrictedZone);

    assertDoesNotThrow(() -> zoneService.deleteZone(restrictedZone.getId()));
    verify(zoneRepository).delete(restrictedZone);
  }

  @Test
  void deleteZone_notFound_shouldThrowException() {
    UUID nonExistentId = UUID.randomUUID();
    when(zoneRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    AppError exception = assertThrows(AppError.class, () -> zoneService.deleteZone(nonExistentId));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("Zone not found", exception.getMessage());
  }

  @Test
  void findZoneById_success() {
    when(zoneRepository.findById(restrictedZone.getId())).thenReturn(Optional.of(restrictedZone));

    Zone zone = zoneService.findZoneById(restrictedZone.getId());

    assertNotNull(zone);
    assertEquals(restrictedZone.getId(), zone.getId());
  }

  @Test
  void findZoneById_notFound_shouldThrowException() {
    UUID nonExistentId = UUID.randomUUID();
    when(zoneRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    AppError exception = assertThrows(AppError.class, () -> zoneService.findZoneById(nonExistentId));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("Zone not found", exception.getMessage());
  }
}
