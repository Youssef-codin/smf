package com.smf.service.device;

import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.dto.device.DeviceResponse;
import com.smf.dto.device.DeviceTestRequest;
import com.smf.model.Device;
import com.smf.model.User;
import com.smf.repo.DeviceRepository;
import com.smf.repo.UserRepository;
import com.smf.util.AppError;
import java.sql.Timestamp;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService implements IDeviceService {

  private final DeviceRepository deviceRepository;
  private final UserRepository userRepository;
  private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

  public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository) {
    this.deviceRepository = deviceRepository;
    this.userRepository = userRepository;
  }

  public void test(DeviceTestRequest request) {
    logger.info("Received test payload from device: {}", request);
  }

  @Override
  @Transactional
  public DeviceResponse registerDevice(DeviceRegisterRequest request) {

    if (deviceRepository.findByMacAddress(request.getMacAddress()).isPresent()) {
      throw new AppError(HttpStatus.CONFLICT, "Device already registered");
    }

    Timestamp now = new Timestamp(System.currentTimeMillis());
    if (request.getLastSeenTimestamp().after(now)) {
      throw new AppError(HttpStatus.BAD_REQUEST, "last_seen_timestamp cannot be in the future");
    }

    User owner =
        userRepository
            .findById(UUID.fromString(request.getOwnerId()))
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Owner not found"));

    Device device =
        new Device(
            request.getMacAddress(),
            owner,
            request.getLastLocationLat(),
            request.getLastLocationLon(),
            request.getLastSeenTimestamp());

    device = deviceRepository.save(device);

    return new DeviceResponse(
        device.getId(),
        device.getMacAddress(),
        device.getOwner().getId(),
        device.getLastLocationLat(),
        device.getLastLocationLon(),
        device.getLastSeenTimestamp(),
        device.getStatus());
  }

  @Override
  public DeviceResponse getDeviceById(UUID deviceId) {
    Device device =
        deviceRepository
            .findById(deviceId)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Device not found"));

    return new DeviceResponse(
        device.getId(),
        device.getMacAddress(),
        device.getOwner().getId(),
        device.getLastLocationLat(),
        device.getLastLocationLon(),
        device.getLastSeenTimestamp(),
        device.getStatus());
  }
}
