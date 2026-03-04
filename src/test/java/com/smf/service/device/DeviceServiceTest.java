package com.smf.service.device;

import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.dto.device.DeviceResponse;
import com.smf.model.User;
import com.smf.model.enums.DeviceStatus;
import com.smf.repo.DeviceRepository;
import com.smf.repo.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeviceService deviceService;

    private UUID ownerId;
    private User owner;
    private DeviceRegisterRequest request;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();

        owner = new User();
        owner.setId(ownerId);
        owner.setEmail("owner@test.com");

        request = new DeviceRegisterRequest(
                "AA:BB:CC:DD:EE:FF",
                ownerId.toString(),
                10.0,
                20.0,
                new Timestamp(System.currentTimeMillis()),
                DeviceStatus.ONLINE
        );
    }

    @Test
    void registerDevice_success() {
        when(deviceRepository.findByMacAddress(request.getMacAddress()))
                .thenReturn(Optional.empty());

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));

        when(deviceRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DeviceResponse response = deviceService.registerDevice(request);

        assertEquals(request.getMacAddress(), response.macAddress());
        assertEquals(ownerId, response.ownerId());
        assertEquals(request.getLastLocationLat(), response.lastLocationLat());
        assertEquals(request.getLastLocationLon(), response.lastLocationLon());
        assertEquals(request.getLastSeenTimestamp(), response.lastSeenTimestamp());
    }

    @Test
    void registerDevice_conflict() {
        when(deviceRepository.findByMacAddress(request.getMacAddress()))
                .thenReturn(Optional.of(mock(com.smf.model.Device.class)));

        AppError exception =
                assertThrows(AppError.class,
                        () -> deviceService.registerDevice(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void getDeviceById_success() {
        com.smf.model.Device device =
                new com.smf.model.Device(
                        request.getMacAddress(),
                        owner,
                        request.getLastLocationLat(),
                        request.getLastLocationLon(),
                        request.getLastSeenTimestamp()
                );

        UUID deviceId = UUID.randomUUID();
        device.setId(deviceId);

        when(deviceRepository.findById(deviceId))
                .thenReturn(Optional.of(device));

        DeviceResponse response = deviceService.getDeviceById(deviceId);

        assertEquals(device.getMacAddress(), response.macAddress());
        assertEquals(ownerId, response.ownerId());
    }

    @Test
    void getDeviceById_notFound() {
        UUID deviceId = UUID.randomUUID();

        when(deviceRepository.findById(deviceId))
                .thenReturn(Optional.empty());

        AppError exception =
                assertThrows(AppError.class,
                        () -> deviceService.getDeviceById(deviceId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Device not found", exception.getMessage());
    }

    @Test
    void handleSos_updatesStatus() {
        com.smf.model.Device device =
                new com.smf.model.Device(
                        request.getMacAddress(),
                        owner,
                        request.getLastLocationLat(),
                        request.getLastLocationLon(),
                        request.getLastSeenTimestamp()
                );

        when(deviceRepository.findByMacAddress(device.getMacAddress()))
                .thenReturn(Optional.of(device));

        when(deviceRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DeviceResponse response =
                deviceService.handleSos(device.getMacAddress());

        assertEquals(DeviceStatus.SOS, response.status());
    }

    @Test
    void handleOffline_updatesStatus() {
        com.smf.model.Device device =
                new com.smf.model.Device(
                        request.getMacAddress(),
                        owner,
                        request.getLastLocationLat(),
                        request.getLastLocationLon(),
                        request.getLastSeenTimestamp()
                );

        when(deviceRepository.findByMacAddress(device.getMacAddress()))
                .thenReturn(Optional.of(device));

        when(deviceRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DeviceResponse response =
                deviceService.handleOffline(device.getMacAddress());

        assertEquals(DeviceStatus.OFFLINE, response.status());
    }
}