package com.hostel.management.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Keep-alive (heartbeat) mechanism to ping cloud MySQL database (Aiven)
 * periodically to prevent automatic shutdown/idling due to inactivity.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseHeartbeatService {

    private final JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Executes every 10 minutes (600,000 ms) using the existing HikariCP connection pool.
     * Uses lightweight 'SELECT 1;' query to maintain an active database session.
     */
    @Scheduled(fixedRate = 600000, initialDelay = 60000)
    public void pingDatabase() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            String timestamp = LocalDateTime.now().format(FORMATTER);
            System.out.println(timestamp + " [DB Heartbeat] Ping successful");
            log.info("[DB Heartbeat] Ping successful");
        } catch (Exception e) {
            System.err.println("[DB Heartbeat Error] " + e.getMessage());
            log.error("[DB Heartbeat Error] {}", e.getMessage());
        }
    }
}
