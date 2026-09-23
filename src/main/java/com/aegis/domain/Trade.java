package com.aegis.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Trade {
    private final UUID id;
    private final String exrernalReference;
    private final CurrencyPair currencyPair;
    private final BigDecimal amount;
    private TradeStatus status;
    private final Instant createdAt;

    public Trade (UUID id, String exrernalReference, CurrencyPair currencyPair, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Trade amount must be strictly positive.");
        }

        this.id = id;
        this.exrernalReference = exrernalReference;
        this.currencyPair = currencyPair;
        this.amount = amount;
        this.status = TradeStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void markAsValidated() {
        if (this.status != TradeStatus.PENDING) {
            throw new IllegalStateException("Only PENDING trades can be validated.");
        }

        this.status = TradeStatus.VALIDATED;
    }

    public void markAsFailed() {
        this.status = TradeStatus.FAILED;
    }

    public UUID getId() {return id;}
    public String getExrernalReference() {return exrernalReference;}
    public CurrencyPair getCurrencyPair() {return currencyPair;}
    public BigDecimal getAmount() {return amount;}
    public TradeStatus getStatus() {return status;}
    public Instant getCreatedAt() {return createdAt;}
}
