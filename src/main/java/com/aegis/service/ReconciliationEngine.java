package com.aegis.service;

import com.aegis.domain.Trade;
import com.aegis.infrastructure.persistence.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReconciliationEngine {
    private static final Logger log = LoggerFactory.getLogger(ReconciliationEngine.class);
    private final TradeRepository tradeRepository;

    public ReconciliationEngine(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Transactional
    public void submitTrade(Trade trade) {
        log.info("Persisting new Trade ID: {}", trade.getId());
        tradeRepository.save(trade);
    }

    @Transactional
    public void processTrade(Trade trade) {
        log.info("Start reconciliation for Trade ID: {} [{}]", trade.getId(), trade.getExrernalReference());

        try {
            trade.markAsValidated();
            tradeRepository.save(trade);
            log.info("Trade {} successfuly validated. Amount {} {}", trade.getId(), trade.getAmount(), trade.getCurrencyPair());
        } catch (IllegalStateException e) {
            log.error("Reconciliation failed for Trade ID: {} - Reason: {}", trade.getId(), e.getMessage());
            trade.markAsFailed();
            tradeRepository.save(trade);
        }
    }
}
