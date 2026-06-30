package com.portfolio.api.controller;

import com.portfolio.api.dto.PortfolioPerformanceRequest;
import com.portfolio.api.dto.PortfolioPerformanceResponse;
import com.portfolio.api.service.PortfolioPerformanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/performance")
public class PortfolioPerformanceController {

	private final PortfolioPerformanceService portfolioPerformanceService;

	public PortfolioPerformanceController(PortfolioPerformanceService portfolioPerformanceService) {
		this.portfolioPerformanceService = portfolioPerformanceService;
	}

	@PostMapping("/daily-return")
	public ResponseEntity<PortfolioPerformanceResponse> calculateDailyReturn(
			@RequestBody PortfolioPerformanceRequest request) {
		PortfolioPerformanceResponse response = portfolioPerformanceService.calculateDailyReturn(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
