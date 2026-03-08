package com.smf.service.device;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.dto.device.DeviceResponse;
import com.smf.model.Device;
import com.smf.model.User;
import com.smf.model.enums.DeviceStatus;
import com.smf.repo.DeviceRepository;
import com.smf.service.user.IUserService;
import com.smf.util.AppError;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

  @Mock private DeviceRepository deviceRepository;

  @Mock private IUserService userService;

  @InjectMocks private DeviceService deviceService;

  private UUID ownerId;
  private User owner;
  private DeviceRegisterRequest request;

  @BeforeEach
  void setUp() {
    ownerId = UUID.randomUUID();

    owner = new User();
    owner.setId(ownerId);
    owner.setEmail("owner@test.com");

    request =
        new DeviceRegisterRequest(
            "AA:BB:CC:DD:EE:FF",
            ownerId.toString(),
            10.0,
            20.0,
            new Timestamp(System.currentTimeMillis()),
            DeviceStatus.ONLINE);
  }

  @Test
  void registerDevice_success() {

    when(deviceRepository.findByMacAddress(request.getMacAddress())).thenReturn(Optional.empty());

    when(userService.findUserById(ownerId)).thenReturn(owner);

    when(deviceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    DeviceResponse response = deviceService.registerDevice(request);

    assertEquals(request.getMacAddress(), response.macAddress());
    assertEquals(ownerId, response.ownerId());
  }

  @Test
  void registerDevice_conflict() {

    when(deviceRepository.findByMacAddress(request.getMacAddress()))
        .thenReturn(Optional.of(mock(Device.class)));

    AppError exception = assertThrows(AppError.class, () -> deviceService.registerDevice(request));

    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }

  @Test
  void getDeviceById_success() {

    Device device =
        new Device(
            request.getMacAddress(),
            owner,
            request.getLastLocationLat(),
            request.getLastLocationLon(),
            request.getLastSeenTimestamp());

    UUID deviceId = UUID.randomUUID();
    device.setId(deviceId);

    when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));

    DeviceResponse response = deviceService.getDeviceById(deviceId);

    assertEquals(device.getMacAddress(), response.macAddress());
    assertEquals(ownerId, response.ownerId());
  }

  @Test
  void getDeviceById_notFound() {

    UUID deviceId = UUID.randomUUID();

    when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

    AppError exception = assertThrows(AppError.class, () -> deviceService.getDeviceById(deviceId));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("Device not found", exception.getMessage());
  }

  @Test
  void updateDevice_success() {

    UUID deviceId = UUID.randomUUID();

    Device device =
        new Device(
            request.getMacAddress(),
            owner,
            request.getLastLocationLat(),
            request.getLastLocationLon(),
            request.getLastSeenTimestamp());

    when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));

    when(userService.findUserById(ownerId)).thenReturn(owner);

    when(deviceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    DeviceResponse response = deviceService.updateDevice(deviceId, request);

    assertEquals(request.getLastLocationLat(), response.lastLocationLat());
    assertEquals(request.getStatus(), response.status());
  }

  @Test
  void updateDevice_deviceNotFound() {

    UUID deviceId = UUID.randomUUID();

    when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

    AppError exception =
        assertThrows(AppError.class, () -> deviceService.updateDevice(deviceId, request));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("Device not found", exception.getMessage());
  }

  @Test
  void updateDevice_ownerNotFound() {

    UUID deviceId = UUID.randomUUID();

    Device device =
        new Device(
            request.getMacAddress(),
            owner,
            request.getLastLocationLat(),
            request.getLastLocationLon(),
            request.getLastSeenTimestamp());

    when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));

    when(userService.findUserById(ownerId)).thenThrow(new AppError(HttpStatus.NOT_FOUND, "Owner not found"));

    AppError exception =
        assertThrows(AppError.class, () -> deviceService.updateDevice(deviceId, request));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("Owner not found", exception.getMessage());
  }

  @Test
  void deleteDevice_success() {

    UUID deviceId = UUID.randomUUID();

    Device device =
        new Device(
            request.getMacAddress(),
            owner,
            request.getLastLocationLat(),
            request.getLastLocationLon(),
            request.getLastSeenTimestamp());

    when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));

    assertDoesNotThrow(() -> deviceService.deleteDevice(deviceId));

    verify(deviceRepository).delete(device);
  }

  @Test
  void deleteDevice_notFound() {

    UUID deviceId = UUID.randomUUID();

    when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

    AppError exception = assertThrows(AppError.class, () -> deviceService.deleteDevice(deviceId));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("Device not found", exception.getMessage());
  }

  @Test
  void handleSos_updatesStatus() {

    Device device =
        new Device(
            request.getMacAddress(),
            owner,
            request.getLastLocationLat(),
            request.getLastLocationLon(),
            request.getLastSeenTimestamp());

    when(deviceRepository.findByMacAddress(device.getMacAddress())).thenReturn(Optional.of(device));

    when(deviceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    DeviceResponse response = deviceService.handleSos(device.getMacAddress());

    assertEquals(DeviceStatus.SOS, response.status());
  }

  @Test
  void handleOffline_updatesStatus() {

    Device device =
        new Device(
            request.getMacAddress(),
            owner,
            request.getLastLocationLat(),
            request.getLastLocationLon(),
            request.getLastSeenTimestamp());

    when(deviceRepository.findByMacAddress(device.getMacAddress())).thenReturn(Optional.of(device));

    when(deviceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    DeviceResponse response = deviceService.handleOffline(device.getMacAddress());

    assertEquals(DeviceStatus.OFFLINE, response.status());
  }
}
