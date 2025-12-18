package com.smf.service;

import com.smf.dto.request.auth.DeviceRegisterRequest;
import com.smf.dto.response.DeviceRegisterResponse;
import com.smf.exception.user.UserNotFoundException;
import com.smf.model.Device;
import com.smf.model.User;
import com.smf.repo.DeviceRepository;
import com.smf.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class DeviceService implements IDeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public DeviceRegisterResponse registerDevice(DeviceRegisterRequest request) {

        User owner = userRepository.findById(UUID.fromString(request.getOwnerId()))
                .orElseThrow(() -> new UserNotFoundException("Owner not found"));

        if (request.getSerialNumber() == null || request.getSerialNumber().isEmpty()) {
            return new DeviceRegisterResponse(false, "Invalid serial number");
        }

        
        if (deviceRepository.findByDevice_id(request.getDeviceId()).isPresent()) {
            return new DeviceRegisterResponse(false, "Device already registered");
        }

        Device device = new Device();
        device.setId(UUID.randomUUID()); 
        device.setDevice_id(request.getDeviceId());
        device.setDevice_name(request.getDeviceName());
        device.setOwner(owner);
        device.setLast_location_lat(request.getLastLocationLat());
        device.setLast_location_lon(request.getLastLocationLon());
        device.setLast_seen_timestamp(new Timestamp(System.currentTimeMillis()));

        deviceRepository.save(device);

        return new DeviceRegisterResponse(true, "Device registered successfully");
    }
}
