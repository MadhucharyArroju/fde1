package com.portfolio.api.service;

import com.portfolio.api.dto.PortfolioPerformanceRequest;
import com.portfolio.api.dto.PortfolioPerformanceResponse;
import com.portfolio.api.exception.InvalidPortfolioPerformanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Portfolio Performance Service Tests")
class PortfolioPerformanceServiceTest {

	private PortfolioPerformanceService service;

	@BeforeEach
	void setUp() {
		service = new PortfolioPerformanceService();
	}

	@Test
	@DisplayName("Valid scenario: Should return VALID status with correct calculations")
	void testValidScenario() {
		// Given
		PortfolioPerformanceRequest request = PortfolioPerformanceRequest.builder()
				.portfolioId("PF-1001")
				.valuationDate("2026-06-14")
				.beginMarketValues(new BigDecimal("1000000"))
				.endMarketValues(new BigDecimal("1035000"))
				.netCashFlow(new BigDecimal("10000"))
				.beginmarkReturnPct(new BigDecimal("1.8"))
				.currency("USD")
				.requestBy("advisor01")
				.build();

		// When
		PortfolioPerformanceResponse response = service.calculateDailyReturn(request);

		// Then
		assertEquals("PF-1001", response.getPortfolioId());
		assertEquals("2026-06-14", response.getValuationDate());
		assertEquals("VALID", response.getStatus());
		assertNull(response.getReasons());
		assertEquals(new BigDecimal("2.50"), response.getPortfolioRetuenPct());
		assertEquals(new BigDecimal("1.8"), response.getBeginMarketReturn());
		assertEquals(new BigDecimal("0.70"), response.getExcessReturnPct());
		assertNotNull(response.getProcessedAt());
	}

	@Test
	@DisplayName("Review required scenario: Portfolio return differs from benchmark by > 5%")
	void testReviewRequiredDueToBenchmarkDifference() {
		// Given
		PortfolioPerformanceRequest request = PortfolioPerformanceRequest.builder()
				.portfolioId("PF-1002")
				.valuationDate("2026-06-14")
				.beginMarketValues(new BigDecimal("1000000"))
				.endMarketValues(new BigDecimal("1100000"))
				.netCashFlow(BigDecimal.ZERO)
				.beginmarkReturnPct(new BigDecimal("1.8"))
				.currency("USD")
				.requestBy("advisor01")
				.build();

		// When
		PortfolioPerformanceResponse response = service.calculateDailyReturn(request);

		// Then
		assertEquals("REVIEW_REQUIRED", response.getStatus());
		assertNotNull(response.getReasons());
		assertTrue(response.getReasons().stream()
				.anyMatch(r -> r.contains("differs from benchmark")));
		assertEquals(new BigDecimal("10.00"), response.getPortfolioRetuenPct());
	}

	@Test
	@DisplayName("Review required scenario: Net cash flow exceeds 20% of begin market value")
	void testReviewRequiredDueToLargeCashFlow() {
		// Given
		PortfolioPerformanceRequest request = PortfolioPerformanceRequest.builder()
				.portfolioId("PF-1003")
				.valuationDate("2026-06-14")
				.beginMarketValues(new BigDecimal("1000000"))
				.endMarketValues(new BigDecimal("1050000"))
				.netCashFlow(new BigDecimal("250000"))
				.beginmarkReturnPct(new BigDecimal("2.0"))
				.currency("USD")
				.requestBy("advisor01")
				.build();

		// When
		PortfolioPerformanceResponse response = service.calculateDailyReturn(request);

		// Then
		assertEquals("REVIEW_REQUIRED", response.getStatus());
		assertNotNull(response.getReasons());
//		assertTrue(response.getReasons().stream()
//				.anyMatch(r -> r.contains("exceeds 20%")));
	}

	@Test
	@DisplayName("Invalid scenario: Negative begin market value")
	void testNegativeBeginMarketValue() {
		// Given
		PortfolioPerformanceRequest request = PortfolioPerformanceRequest.builder()
				.portfolioId("PF-1004")
				.valuationDate("2026-06-14")
				.beginMarketValues(new BigDecimal("-100000"))
				.endMarketValues(new BigDecimal("1000000"))
				.netCashFlow(BigDecimal.ZERO)
				.beginmarkReturnPct(new BigDecimal("2.0"))
				.currency("USD")
				.requestBy("advisor01")
				.build();

		// Then
		assertThrows(InvalidPortfolioPerformanceException.class, () ->
				service.calculateDailyReturn(request));
	}

	@Test
	@DisplayName("Invalid scenario: Negative end market value")
	void testNegativeEndMarketValue() {
		// Given
		PortfolioPerformanceRequest request = PortfolioPerformanceRequest.builder()
				.portfolioId("PF-1005")
				.valuationDate("2026-06-14")
				.beginMarketValues(new BigDecimal("1000000"))
				.endMarketValues(new BigDecimal("-50000"))
				.netCashFlow(BigDecimal.ZERO)
				.beginmarkReturnPct(new BigDecimal("2.0"))
				.currency("USD")
				.requestBy("advisor01")
				.build();

		// Then
		assertThrows(InvalidPortfolioPerformanceException.class, () ->
				service.calculateDailyReturn(request));
	}

	@Test
	@DisplayName("Invalid scenario: Missing currency")
	void testMissingCurrency() {
		// Given
		PortfolioPerformanceRequest request = PortfolioPerformanceRequest.builder()
				.portfolioId("PF-1006")
				.valuationDate("2026-06-14")
				.beginMarketValues(new BigDecimal("1000000"))
				.endMarketValues(new BigDecimal("1035000"))
				.netCashFlow(BigDecimal.ZERO)
				.beginmarkReturnPct(new BigDecimal("2.0"))
				.currency(null)
				.requestBy("advisor01")
				.build();

		// Then
		assertThrows(InvalidPortfolioPerformanceException.class, () ->
				service.calculateDailyReturn(request));
	}

	@Test
	@DisplayName("Invalid scenario: BeginValue is 0 and endValue is not 0")
	void testBeginValueZeroWithNonZeroEndValue() {
		// Given
		PortfolioPerformanceRequest request = PortfolioPerformanceRequest.builder()
				.portfolioId("PF-1007")
				.valuationDate("2026-06-14")
				.beginMarketValues(BigDecimal.ZERO)
				.endMarketValues(new BigDecimal("1000000"))
				.netCashFlow(BigDecimal.ZERO)
				.beginmarkReturnPct(new BigDecimal("2.0"))
				.currency("USD")
				.requestBy("advisor01")
				.build();

		// Then
		assertThrows(InvalidPortfolioPerformanceException.class, () ->
				service.calculateDailyReturn(request));
	}

	@Test
	@DisplayName("Edge case: BeginValue is 0 and endValue is also 0 should be valid")
	void testBeginValueZeroWithZeroEndValue() {
		// Given
		PortfolioPerformanceRequest request = PortfolioPerformanceRequest.builder()
				.portfolioId("PF-1008")
				.valuationDate("2026-06-14")
				.beginMarketValues(BigDecimal.ZERO)
				.endMarketValues(BigDecimal.ZERO)
				.netCashFlow(BigDecimal.ZERO)
				.beginmarkReturnPct(new BigDecimal("2.0"))
				.currency("USD")
				.requestBy("advisor01")
				.build();

		// When
		PortfolioPerformanceResponse response = service.calculateDailyReturn(request);

		// Then
		assertEquals("VALID", response.getStatus());
		assertEquals(new BigDecimal("0.00"), response.getPortfolioRetuenPct());
	}

	@Test
	@DisplayName("Calculation precision test: Verify portfolio return calculation accuracy")
	void testCalculationPrecision() {
		// Given: beginValue=1M, endValue=1.035M, netCash=10k -> return = (1.035M - 1M - 10k)/1M * 100 = 2.5%
		PortfolioPerformanceRequest request = PortfolioPerformanceRequest.builder()
				.portfolioId("PF-1009")
				.valuationDate("2026-06-14")
				.beginMarketValues(new BigDecimal("1000000"))
				.endMarketValues(new BigDecimal("1035000"))
				.netCashFlow(new BigDecimal("10000"))
				.beginmarkReturnPct(new BigDecimal("1.8"))
				.currency("USD")
				.requestBy("advisor01")
				.build();

		// When
		PortfolioPerformanceResponse response = service.calculateDailyReturn(request);

		// Then: Verify exact calculation
		BigDecimal expectedReturn = new BigDecimal("2.50");
		assertEquals(expectedReturn, response.getPortfolioRetuenPct());
	}
}
