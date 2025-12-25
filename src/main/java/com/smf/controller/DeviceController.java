package com.smf.controller;

import com.smf.dto.device.DeviceRegisterRequest;
import com.smf.dto.response.api.ApiResponse;
import com.smf.model.Device;
import com.smf.service.device.IDeviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/device")
public class DeviceController {

    private final IDeviceService deviceService;

    public DeviceController(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerDevice(
            @Valid @RequestBody DeviceRegisterRequest request) {

        Device device = deviceService.registerDevice(request);

        return ResponseEntity.ok(
                new ApiResponse(true, "Device registered successfully", device)
        );
    }
}
