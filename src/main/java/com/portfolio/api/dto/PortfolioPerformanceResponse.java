package com.portfolio.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortfolioPerformanceResponse {
	private String portfolioId;
	private String valuationDate;
	private BigDecimal portfolioRetuenPct;
	private BigDecimal beginMarketReturn;
	private BigDecimal excessReturnPct;
	private String status;
	private List<String> reasons;
	private ZonedDateTime processedAt;
}
