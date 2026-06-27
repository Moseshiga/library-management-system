package com.moseshiga.librarymanagement.monitoring;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabasePerformanceHealthIndicator implements HealthIndicator {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        long startTime = System.currentTimeMillis();
        try {
            jdbcTemplate.execute("SELECT 1");
            long executionTime = System.currentTimeMillis() - startTime;

            if (executionTime > 1000) {
                return Health.down()
                        .withDetail("error", "Database is responding too slowly")
                        .withDetail("executionTimeMs", executionTime)
                        .build();
            }

            return Health.up()
                    .withDetail("database", "PostgreSQL/H2")
                    .withDetail("executionTimeMs", executionTime)
                    .build();

        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", "Cannot connect to database")
                    .withException(e)
                    .build();
        }
    }
}