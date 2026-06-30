package com.portfolio.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioPerformanceRequest {
	private String portfolioId;
	private String valuationDate;
	private BigDecimal beginMarketValues;
	private BigDecimal endMarketValues;
	private BigDecimal netCashFlow;
	private BigDecimal beginmarkReturnPct;
	private String currency;
	private String requestBy;
}
