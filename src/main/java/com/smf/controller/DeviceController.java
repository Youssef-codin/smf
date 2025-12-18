package com.smf.controller;

import com.smf.dto.request.auth.DeviceRegisterRequest;
import com.smf.dto.response.DeviceRegisterResponse;
import com.smf.service.IDeviceService;
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
    public ResponseEntity<DeviceRegisterResponse> registerDevice(
            @Valid @RequestBody DeviceRegisterRequest request) {

        DeviceRegisterResponse response = deviceService.registerDevice(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
