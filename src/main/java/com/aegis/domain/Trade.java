package com.aegis.domain;

import jakarta.persistence.*;
import org.springframework.beans.propertyeditors.CurrencyEditor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trades")
public class Trade {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String exrernalReference;

    @Column(nullable = false, length = 7)
    private CurrencyPair currencyPair;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Trade () {}

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
