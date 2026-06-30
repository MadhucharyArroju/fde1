package com.portfolio.api.exception;

public class InvalidPortfolioPerformanceException extends RuntimeException {
	public InvalidPortfolioPerformanceException(String message) {
		super(message);
	}

	public InvalidPortfolioPerformanceException(String message, Throwable cause) {
		super(message, cause);
	}
}
