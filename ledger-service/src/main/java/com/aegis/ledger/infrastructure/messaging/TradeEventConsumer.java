package com.aegis.ledger.infrastructure.messaging;

import com.aegis.ledger.domain.TradeEventDto;
import com.aegis.ledger.infrastructure.redis.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class TradeEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(TradeEventConsumer.class);
    private final ObjectMapper objectMapper;
    private final IdempotencyService idempotencyService;

    public TradeEventConsumer(ObjectMapper objectMapper, IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "aegis.trades.events", groupId = "aegis-ledger-group-v3")
    public void consumeTradeEvent(String payload) {
        log.info("=== RAW KAFKA PAYLOAD RECEIVED ===\n{}", payload);
        try {
            TradeEventDto event = objectMapper.readValue(payload, TradeEventDto.class);

            String idempotencyKey = "ledger:trade:"+event.id()+":status:"+event.status();

            if (idempotencyService.isDuplicate(idempotencyKey)) {
                return;
            }

            log.info("LEDGER SERVICE: Processing NEW validated trade. Crediting {} {} for Trade ID: {}",
                    event.amount(), event.externalReference(), event.id()
            );
        } catch (JacksonException e) {
            log.error("LEDGER SERVICE: Failed to parse Kafka message payload. Sending to DLQ...", e);
        } catch (Exception e) {
            log.error("LEDGER SERVICE: Unexpected fatal error during processing.", e);
        }
    }
}
