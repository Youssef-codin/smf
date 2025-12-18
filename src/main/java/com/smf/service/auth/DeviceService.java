package com.smf.service.auth;

import com.smf.dto.request.auth.DeviceRegisterRequest;
import com.smf.dto.response.DeviceRegisterResponse;
import com.smf.model.Device;
import com.smf.model.User;
import com.smf.repo.DeviceRepository;
import com.smf.repo.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    public DeviceRegisterResponse registerDevice(DeviceRegisterRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Device device = new Device();
        device.setDeviceId(UUID.randomUUID().toString());
        device.setDeviceName(request.getDeviceName());
        device.setLastLocationLat(request.getLastLocationLat());
        device.setLastLocationLon(request.getLastLocationLon());
        device.setOwner(owner);
        device.setLastSeenTimestamp(LocalDateTime.now());

        deviceRepository.save(device);

        return new DeviceRegisterResponse(device.getDeviceId(), true, "Device registered successfully");
    }
}
