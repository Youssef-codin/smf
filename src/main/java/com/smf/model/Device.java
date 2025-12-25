package com.smf.model;

import java.sql.Timestamp;
import java.util.UUID;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "devices")
public class Device {
	@Id
	private UUID id;
	private String device_id;
	@ManyToOne
	@JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
	private User owner;
	private String device_name;
	private Double last_location_lat;
	private Double last_location_lon;
	private Timestamp last_seen_timestamp;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String name) {
		this.device_name = name;
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
}
