package com.aegis.service;

import com.aegis.domain.OutboxEvent;
import com.aegis.domain.Trade;
import com.aegis.infrastructure.persistence.OutboxEventRepository;
import com.aegis.infrastructure.persistence.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
public class ReconciliationEngine {
    private static final Logger log = LoggerFactory.getLogger(ReconciliationEngine.class);
    private final TradeRepository tradeRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public ReconciliationEngine(TradeRepository tradeRepository, OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.tradeRepository = tradeRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void submitTrade(Trade trade) {
        log.info("Persisting new Trade ID: {}", trade.getId());
        tradeRepository.save(trade);
    }

    @Transactional
    public void processTrade(Trade trade) {
        log.info("Start reconciliation for Trade ID: {} [{}]", trade.getId(), trade.getExternalReference());

        try {
            trade.markAsValidated();
            tradeRepository.save(trade);

            publishToOutbox(trade, "TRADE_VALIDATED");

            log.info("Trade {} successfuly validated", trade.getId());
        } catch (IllegalStateException e) {
            log.error("Reconciliation failed for Trade ID: {} - Reason: {}", trade.getId(), e.getMessage());
            trade.markAsFailed();
            tradeRepository.save(trade);
            publishToOutbox(trade, "TRADE_FAILED");
        }
    }

    private void publishToOutbox(Trade trade, String eventType) {
        try {
            String payload = objectMapper.writeValueAsString(trade);
            OutboxEvent event = new OutboxEvent(UUID.randomUUID(), "TRADE", trade.getId().toString(), eventType, payload);
            outboxEventRepository.save(event);
        }catch (JacksonException e){
            log.error("Failed to serialize Trade to JSON for outbox event", e);
            throw new RuntimeException("JSON serialization failed", e);
        }
    }
}
