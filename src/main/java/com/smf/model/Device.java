package com.smf.model;

import java.sql.Timestamp;
import java.util.UUID;

import com.smf.model.enums.DeviceStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "devices")
public class Device {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "mac_address", unique = true, nullable = false)
	private String macAddress;

	@ManyToOne
	@JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
	private User owner;

	@Column(name = "last_location_lat")
	private Double lastLocationLat;

	@Column(name = "last_location_lon")
	private Double lastLocationLon;

	@Column(name = "last_seen_timestamp")
	private Timestamp lastSeenTimestamp;

	@Enumerated(EnumType.STRING)
	private DeviceStatus status;

	public Device() {}

	public Device(String macAddress, User owner,
	              Double lastLocationLat,
	              Double lastLocationLon,
	              Timestamp lastSeenTimestamp) {
		this.macAddress = macAddress;
		this.owner = owner;
		this.lastLocationLat = lastLocationLat;
		this.lastLocationLon = lastLocationLon;
		this.lastSeenTimestamp = lastSeenTimestamp;
		this.status = DeviceStatus.OFFLINE;
	}


	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Double getLastLocationLat() {
		return lastLocationLat;
	}

	public void setLastLocationLat(Double lastLocationLat) {
		this.lastLocationLat = lastLocationLat;
	}

	public Double getLastLocationLon() {
		return lastLocationLon;
	}

	public void setLastLocationLon(Double lastLocationLon) {
		this.lastLocationLon = lastLocationLon;
	}

	public Timestamp getLastSeenTimestamp() {
		return lastSeenTimestamp;
	}

	public void setLastSeenTimestamp(Timestamp lastSeenTimestamp) {
		this.lastSeenTimestamp = lastSeenTimestamp;
	}

	public DeviceStatus getStatus() {
		return status;
	}

	public void setStatus(DeviceStatus status) {
		this.status = status;
	}
}