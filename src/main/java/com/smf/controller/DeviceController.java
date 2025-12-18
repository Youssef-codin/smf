package com.smf.controller;

import com.smf.dto.request.auth.DeviceRegisterRequest;
import com.smf.dto.response.DeviceRegisterResponse;
import com.smf.service.auth.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/device")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/register")
    public ResponseEntity<DeviceRegisterResponse> registerDevice(@RequestBody DeviceRegisterRequest request) {
        DeviceRegisterResponse response = deviceService.registerDevice(request);
        return ResponseEntity.ok(response);
    }
}
