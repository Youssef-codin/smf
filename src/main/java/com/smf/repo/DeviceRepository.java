package com.smf.repo;

import com.smf.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface DeviceRepository extends JpaRepository<Device, String> {
    
    Optional<Device> findByDevice_id(String device_id);
}
