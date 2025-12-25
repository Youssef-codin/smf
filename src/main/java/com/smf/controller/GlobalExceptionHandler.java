package com.smf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.smf.dto.api.ApiResponse;
import com.smf.util.AppError;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(AppError.class)
	public ResponseEntity<ApiResponse> handleAppError(AppError e) {
		return ResponseEntity.status(e.getStatus()).body(new ApiResponse(false, e.getMessage(), null));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> handleGeneralError(Exception e) {
		log.error("Unhandled Exception", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiResponse(false, e.getMessage(), null));
	}
}
