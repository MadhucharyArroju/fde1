package com.portfolio.api.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
	private String status;
	private String message;
	private ZonedDateTime timestamp;
}
