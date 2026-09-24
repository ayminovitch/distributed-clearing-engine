package com.aegis.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Boolean processed;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected OutboxEvent() {}

    public OutboxEvent(UUID id, String aggregateType, String aggregateId, String eventType, String payload) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.processed = false;
        this.createdAt = Instant.now();
    }

    public void markAsProcessed() {
        this.processed = true;
    }

    public UUID getId(){return id;}
    public String getAggregateType(){return aggregateType;}
    public String getAggregateId(){return aggregateId;}
    public String getEventType(){return eventType;}
    public String getPayload(){return payload;}
    public Instant getCreatedAt(){return createdAt;}
    public boolean isProcessed(){return processed;}
}
