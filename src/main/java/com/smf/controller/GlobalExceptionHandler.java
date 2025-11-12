package com.smf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.smf.dto.response.api.ApiResponse;
import com.smf.exception.user.UserAlreadyExistsException;
import com.smf.exception.user.UserNotFoundException;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private ResponseEntity<ApiResponse> responseHelper(HttpStatus status, String error) {
		return ResponseEntity.status(status).body(new ApiResponse(false, error, null));
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ApiResponse> handleUserExists(UserAlreadyExistsException e) {
		return responseHelper(HttpStatus.CONFLICT, e.getMessage());
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiResponse> handleUserNotFound(UserNotFoundException e) {
		return responseHelper(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ApiResponse> handleJwtSignatureException(JwtException e) {
		return responseHelper(HttpStatus.UNAUTHORIZED, e.getMessage());
	}

	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<ApiResponse> handleJwtSignatureException(SignatureException e) {
		return responseHelper(HttpStatus.UNAUTHORIZED, e.getMessage());
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiResponse> handleMethodArgError(AuthenticationException e) {
		return responseHelper(HttpStatus.UNAUTHORIZED, e.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse> handleAccessDenied(AccessDeniedException e) {
		return responseHelper(HttpStatus.UNAUTHORIZED, "You are not authorized to access this resource.");
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse> handleMethodArgError(MethodArgumentNotValidException e) {
		return responseHelper(HttpStatus.BAD_REQUEST, "Invalid email, username or password.");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> handleGeneralError(Exception e) {
		log.error("Unhandled Exception", e);
		return responseHelper(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
	}
}
