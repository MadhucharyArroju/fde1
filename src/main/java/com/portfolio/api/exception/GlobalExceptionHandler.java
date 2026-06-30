package com.portfolio.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidPortfolioPerformanceException.class)
	public ResponseEntity<ErrorResponse> handleInvalidPortfolioPerformanceException(
			InvalidPortfolioPerformanceException ex) {
		ErrorResponse errorResponse = new ErrorResponse(
				"INVALID_INPUT",
				ex.getMessage(),
				ZonedDateTime.now()
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse(
				"INTERNAL_SERVER_ERROR",
				"An unexpected error occurred: " + ex.getMessage(),
				ZonedDateTime.now()
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}
