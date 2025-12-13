# Week 2 Backend Todo

1. **Create test endpoint** `POST /api/v1/device/test`
   - Takes JSON payload from firmware
   - Logs it
   - Returns proper `ApiResponse` class

2. **Device registration** `POST /api/v1/device/register`
   - Firmware sends device info
   - Returns `device_id` to caller

3. **Get device details** `GET /api/v1/device/{device_id}`
   - Fetch device information
   - **MAKE SURE TO AUTHORIZE** – user can only access their own devices