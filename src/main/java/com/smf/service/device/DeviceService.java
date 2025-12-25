package com.smf.service.device;

import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.util.AppError;
import com.smf.model.Device;
import com.smf.model.User;
import com.smf.repo.DeviceRepository;
import com.smf.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class DeviceService implements IDeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public DeviceService(DeviceRepository deviceRepository,
            UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Device registerDevice(DeviceRegisterRequest request) {

        if (deviceRepository.findByDevice_id(request.getDeviceId()).isPresent()) {
            throw new AppError(HttpStatus.CONFLICT, "Device already registered");
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (request.getLastSeenTimestamp().after(now)) {
            throw new AppError(HttpStatus.BAD_REQUEST, "last_seen_timestamp cannot be in the future");
        }

        UUID authenticatedUserId = getAuthenticatedUserId();
        UUID ownerId = UUID.fromString(request.getOwnerId());

        if (!authenticatedUserId.equals(ownerId)) {
            throw new AppError(HttpStatus.BAD_REQUEST, "You cannot register a device for another user");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Owner not found"));

        Device device = new Device();
        device.setDevice_id(request.getDeviceId());
        device.setDevice_name(request.getDeviceName());
        device.setOwner(owner);
        device.setLast_location_lat(request.getLastLocationLat());
        device.setLast_location_lon(request.getLastLocationLon());
        device.setLast_seen_timestamp(request.getLastSeenTimestamp());

        return deviceRepository.save(device);
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }
}
