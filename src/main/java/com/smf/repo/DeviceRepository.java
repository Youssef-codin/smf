package com.smf.repo;

import com.smf.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

	Optional<Device> findByMacAddress(String macAddress);

@Modifying
  @Query(value = "UPDATE devices SET violation_count = COALESCE(violation_count, 0) + 1 WHERE mac_address = :macAddress", nativeQuery = true)
  int incrementViolationCount(@Param("macAddress") String macAddress);
}
