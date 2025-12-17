package com.smf.controller;

import com.smf.dto.request.device.DeviceTestRequest;
import com.smf.dto.response.api.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/device")
public class DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @PostMapping("/test")
    public ResponseEntity<ApiResponse<DeviceTestRequest>> testDevice(
            @RequestBody @Validated DeviceTestRequest request) {

        // تسجيل البيانات في الـ log
        logger.info("Received test payload from device: {}", request);

        // Response
        ApiResponse<DeviceTestRequest> response = new ApiResponse<>(
                true,
                "Payload received successfully",
                request
        );

        return ResponseEntity.ok(response);
    }
}
