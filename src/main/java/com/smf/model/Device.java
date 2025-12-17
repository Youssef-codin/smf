package com.smf.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @Column(nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "device_id", nullable = false, unique = true)
    private String deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Column(name = "last_location_lat")
    private Double lastLocationLat;

    @Column(name = "last_location_lon")
    private Double lastLocationLon;

    @Column(name = "last_seen_timestamp")
    private LocalDateTime lastSeenTimestamp;

    // Getters and setters
    public UUID getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public Double getLastLocationLat() { return lastLocationLat; }
    public void setLastLocationLat(Double lastLocationLat) { this.lastLocationLat = lastLocationLat; }
    public Double getLastLocationLon() { return lastLocationLon; }
    public void setLastLocationLon(Double lastLocationLon) { this.lastLocationLon = lastLocationLon; }
    public LocalDateTime getLastSeenTimestamp() { return lastSeenTimestamp; }
    public void setLastSeenTimestamp(LocalDateTime lastSeenTimestamp) { this.lastSeenTimestamp = lastSeenTimestamp; }
}
