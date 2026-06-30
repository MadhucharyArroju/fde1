# Portfolio Daily Return Summary API

## Overview
A Spring Boot microservice that calculates and validates portfolio daily return performance metrics. The service accepts portfolio valuation data and returns calculated returns, excess returns, and validation status based on defined business rules.

## Technology Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.2.1
- **Build Tool**: Maven
- **Testing**: JUnit 5 with Spring Boot Test
- **Libraries**: Lombok

## Architecture
The application follows a layered architecture pattern:

`
Controller Layer
    ?
Service Layer (Business Logic & Validation)
    ?
Exception Handling Layer
`

## API Endpoint

### POST /api/performance/daily-return

**Request Payload:**
{
  "portfolioId": "PF-1001",
  "valuationDate": "2026-06-14",
  "beginMarketValues": 1000000,
  "endMarketValues": 1035000,
  "netCashFlow": 10000,
  "beginmarkReturnPct": 1.8,
  "currency": "USD",
  "requestBy": "advisor01"
}

## Business Rules

1. Reject if begin market value is less than 0
2. Reject if end market value is less than 0
3. Reject if currency is missing
4. Calculate portfolio return: ((endValue - beginValue - netCashFlow) / beginValue) * 100
5. Reject if beginValue is 0 and endValue is not 0
6. Set REVIEW_REQUIRED if absolute difference between portfolio return and benchmark return > 5%
7. Set REVIEW_REQUIRED if absolute net cash flow > 20% of begin market value

## Setup & Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build
ash
mvn clean install


### Run Locally
ash
mvn spring-boot:run


The application will start on http://localhost:8080

## Testing

### Run All Tests
ash
mvn test


## Project Structure

- src/main/java/com/portfolio/api/PortfolioPerformanceApiApplication.java (Main)
- src/main/java/com/portfolio/api/controller/PortfolioPerformanceController.java (REST Endpoint)
- src/main/java/com/portfolio/api/service/PortfolioPerformanceService.java (Business Logic)
- src/main/java/com/portfolio/api/dto/ (Request/Response DTOs)
- src/main/java/com/portfolio/api/exception/ (Exception Handling)
- src/test/java/com/portfolio/api/service/PortfolioPerformanceServiceTest.java (Unit Tests)

## Key Assumptions

1. BigDecimal for all monetary values (precision)
2. UTC timestamps (ZonedDateTime)
3. NetCashFlow defaults to 0 if not provided
4. Stateless service (no persistence)
5. Response precision: 2 decimal places
6. Simple design: No database, authentication, or caching
