package com.smf.model;

import com.smf.model.enums.DeviceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
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

  @Column(name = "last_location_lat")
  private Double lastLocationLat;

  @Column(name = "last_location_lon")
  private Double lastLocationLon;

  @Column(name = "last_seen_timestamp")
  private Timestamp lastSeenTimestamp;

  @Enumerated(EnumType.STRING)
  private DeviceStatus status;

  public Device(
      String macAddress,
      User owner,
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
}
