package com.smf.repo;

import com.smf.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    // MUST match the Java field name in Device entity
    Optional<Device> findByDevice_id(String device_id);
}
