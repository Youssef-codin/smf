package com.smf.model;

import java.sql.Timestamp;
import java.util.UUID;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "devices")
public class Device {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@Column(name = "mac_address")
	@NaturalId
	private String macAddress;
	@ManyToOne
	@JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
	private User owner;
	private Double last_location_lat;
	private Double last_location_lon;
	private Timestamp last_seen_timestamp;

	public Device() {
	}

	public Device(String macAddress, User owner, Double last_location_lat, Double last_location_lon,
			Timestamp last_seen_timestamp) {
		this.macAddress = macAddress;
		this.owner = owner;
		this.last_location_lat = last_location_lat;
		this.last_location_lon = last_location_lon;
		this.last_seen_timestamp = last_seen_timestamp;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Double getLast_location_lat() {
		return last_location_lat;
	}

	public void setLast_location_lat(Double last_location_lat) {
		this.last_location_lat = last_location_lat;
	}

	public Double getLast_location_lon() {
		return last_location_lon;
	}

	public void setLast_location_lon(Double last_location_lon) {
		this.last_location_lon = last_location_lon;
	}

	public Timestamp getLast_seen_timestamp() {
		return last_seen_timestamp;
	}

	public void setLast_seen_timestamp(Timestamp last_seen_timestamp) {
		this.last_seen_timestamp = last_seen_timestamp;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String deviceId) {
		this.macAddress = deviceId;
	}
}
