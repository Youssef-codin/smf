package com.smf.dto.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceTestRequest {

  @NotBlank private String macAddress;

  @NotNull private Double lat;

  @NotNull private Double lon;
}
