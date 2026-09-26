package com.aegis.ledger.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TradeEventConsumer {
    public static final Logger log = LoggerFactory.getLogger(TradeEventConsumer.class);

    @KafkaListener(topics = "aegis.trades.events", groupId = "aegis-ledger-group")
    public void consumeTradeEvent(String payload) {
        log.info("LEDGER SERVICE: Received Trade Event from kafka -> {}", payload);
    }
}
