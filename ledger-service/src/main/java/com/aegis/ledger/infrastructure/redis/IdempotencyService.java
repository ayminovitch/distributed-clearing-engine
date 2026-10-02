package com.aegis.ledger.infrastructure.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.time.Duration;

@Service
public class IdempotencyService {
    public static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);

    private final StringRedisTemplate redisTemplate;

    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @CircuitBreaker(name = "redisCache")
    public boolean isDuplicate(String idempotencyKey) {
        Boolean isNewEvent = redisTemplate.opsForValue()
                .setIfAbsent(idempotencyKey, "PROCESSED", Duration.ofHours(24));

        if (Boolean.TRUE.equals(isNewEvent)) {
            return false;
        } else  {
            log.warn("Idempotency lock triggered! Duplicate event detected and ignored: {}", idempotencyKey);
            return true;
        }
    }
}
