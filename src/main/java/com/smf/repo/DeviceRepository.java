package com.smf.repo;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.smf.model.Device;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    // Additional queries can be added if needed
}
