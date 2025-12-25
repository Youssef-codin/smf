package com.smf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smf.dto.api.ApiResponse;
import com.smf.dto.device.DeviceTestRequest;
import com.smf.model.Device;
import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.service.device.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/device")
public class DeviceController {

	private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

	private final IDeviceService deviceService;

	public DeviceController(IDeviceService deviceService) {
		this.deviceService = deviceService;
	}

	@PostMapping("/test")
	public ResponseEntity<ApiResponse> testDevice(@RequestBody @Validated DeviceTestRequest request) {
		logger.info("Received test payload from device: {}", request);
		return ResponseEntity.ok(new ApiResponse(true, "Payload received successfully", request));
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse> registerDevice(@Valid @RequestBody DeviceRegisterRequest request) {

		Device device = deviceService.registerDevice(request);
		return ResponseEntity.ok(new ApiResponse(true, "Device registered successfully", device));
	}
}
