package com.smf.api;

import java.time.Instant;

public record ApiResponse(boolean success, String message, Object data, Instant time) {

	public ApiResponse(boolean success, String message, Object data) {
		this(success, message, data, Instant.now());
	}
}
