package com.portfolio.api.service;

import com.portfolio.api.dto.PortfolioPerformanceRequest;
import com.portfolio.api.dto.PortfolioPerformanceResponse;
import com.portfolio.api.exception.InvalidPortfolioPerformanceException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioPerformanceService {

	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final BigDecimal HUNDRED = new BigDecimal("100");
	private static final BigDecimal FIVE_PERCENT = new BigDecimal("5");
	private static final BigDecimal TWENTY_PERCENT = new BigDecimal("20");

	public PortfolioPerformanceResponse calculateDailyReturn(PortfolioPerformanceRequest request) {
		validateInput(request);

		BigDecimal portfolioReturn = calculatePortfolioReturn(request);
		BigDecimal excessReturn = calculateExcessReturn(portfolioReturn, request.getBeginmarkReturnPct());

		List<String> reasons = new ArrayList<>();
		String status = determineStatus(portfolioReturn, request, reasons);

		return PortfolioPerformanceResponse.builder()
				.portfolioId(request.getPortfolioId())
				.valuationDate(request.getValuationDate())
				.portfolioRetuenPct(portfolioReturn)
				.beginMarketReturn(request.getBeginmarkReturnPct())
				.excessReturnPct(excessReturn)
				.status(status)
				.reasons(reasons.isEmpty() ? null : reasons)
				.processedAt(ZonedDateTime.now())
				.build();
	}

	private void validateInput(PortfolioPerformanceRequest request) {
		// Rule 1: Reject if begin market value is less than 0
		if (request.getBeginMarketValues() != null && request.getBeginMarketValues().compareTo(ZERO) < 0) {
			throw new InvalidPortfolioPerformanceException("Begin market value cannot be negative");
		}

		// Rule 1: Reject if end market value is less than 0
		if (request.getEndMarketValues() != null && request.getEndMarketValues().compareTo(ZERO) < 0) {
			throw new InvalidPortfolioPerformanceException("End market value cannot be negative");
		}

		// Rule 2: Reject if currency is missing
		if (request.getCurrency() == null || request.getCurrency().trim().isEmpty()) {
			throw new InvalidPortfolioPerformanceException("Currency is required");
		}

		// Rule 4: Reject if beginValue is 0 and endValue is not 0
		if (request.getBeginMarketValues().compareTo(ZERO) == 0 &&
			request.getEndMarketValues().compareTo(ZERO) != 0) {
			throw new InvalidPortfolioPerformanceException("Invalid: beginValue is 0 but endValue is not 0");
		}
	}

	private BigDecimal calculatePortfolioReturn(PortfolioPerformanceRequest request) {
		BigDecimal beginValue = request.getBeginMarketValues();
		BigDecimal endValue = request.getEndMarketValues();
		BigDecimal netCashFlow = request.getNetCashFlow() != null ? request.getNetCashFlow() : ZERO;

		// Rule 3: Calculate portfolio return using formula: ((endValue - beginValue - netCashFlow) / beginValue) * 100
		if (beginValue.compareTo(ZERO) > 0) {
			BigDecimal numerator = endValue.subtract(beginValue).subtract(netCashFlow);
			BigDecimal returnPct = numerator.divide(beginValue, 10, RoundingMode.HALF_UP).multiply(HUNDRED);
			return returnPct.setScale(2, RoundingMode.HALF_UP);
		}

		return ZERO.setScale(2, RoundingMode.HALF_UP);
	}

	private BigDecimal calculateExcessReturn(BigDecimal portfolioReturn, BigDecimal benchmarkReturn) {
		if (benchmarkReturn == null) {
			benchmarkReturn = ZERO;
		}
		return portfolioReturn.subtract(benchmarkReturn).setScale(2, RoundingMode.HALF_UP);
	}

	private String determineStatus(BigDecimal portfolioReturn, PortfolioPerformanceRequest request, List<String> reasons) {
		// Rule 5: Set REVIEW_REQUIRED if absolute difference between portfolio return and benchmark return > 5%
		BigDecimal benchmarkReturn = request.getBeginmarkReturnPct() != null ? request.getBeginmarkReturnPct() : ZERO;
		BigDecimal absoluteDifference = portfolioReturn.subtract(benchmarkReturn).abs();

		if (absoluteDifference.compareTo(FIVE_PERCENT) > 0) {
			reasons.add("Portfolio return differs from benchmark by more than 5%");
			return "REVIEW_REQUIRED";
		}

		// Rule 6: Set REVIEW_REQUIRED if absolute net cash flow > 20% of begin market value
		BigDecimal netCashFlow = request.getNetCashFlow() != null ? request.getNetCashFlow() : ZERO;
		BigDecimal absoluteNetCashFlow = netCashFlow.abs();
		BigDecimal twentyPercentOfBeginValue = request.getBeginMarketValues().multiply(TWENTY_PERCENT).divide(HUNDRED, 10, RoundingMode.HALF_UP);

		if (absoluteNetCashFlow.compareTo(twentyPercentOfBeginValue) > 0) {
			reasons.add("Net cash flow exceeds 20% of begin market value");
			return "REVIEW_REQUIRED";
		}

		return "VALID";
	}
}
