package com.aegis.service;

import com.aegis.domain.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReconciliationEngine {
    private static final Logger log = LoggerFactory.getLogger(ReconciliationEngine.class);

    public void processTrade(Trade trade) {
        log.info("Start reconciliation for Trade ID: {} [{}]", trade.getId(), trade.getExrernalReference());

        try {
            trade.markAsValidated();
            log.info("Trade {} successfuly validated. Amount {} {}", trade.getId(), trade.getAmount(), trade.getCurrencyPair());
        } catch (IllegalStateException e) {
            log.error("Reconciliation failed for Trade ID: {} - Reason: {}", trade.getId(), e.getMessage());
            trade.markAsFailed();
        }
    }
}
