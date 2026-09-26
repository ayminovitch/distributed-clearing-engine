package com.aegis.infrastructure.messaging;

import com.aegis.domain.OutboxEvent;
import com.aegis.infrastructure.persistence.OutboxEventRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxRelayScheduler {
    private static final Logger log = LoggerFactory.getLogger(OutboxRelayScheduler.class);
    private static final String TOPIC = "aegis.trades.events";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxRelayScheduler(OutboxEventRepository outboxEventRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void relayEventsToKafka() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findTop50ByProcessedFalseOrderByCreatedAtAsc();

        if (!pendingEvents.isEmpty()) {
            log.info("Relaying {} pending events to Kafka", pendingEvents.size());
        }

        for (OutboxEvent event : pendingEvents) {
            try {
                kafkaTemplate.send(TOPIC, event.getAggregateId(), event.getPayload()).get(5, TimeUnit.SECONDS);
                event.markAsProcessed();
                log.info("Event with ID {} has been relayed to Kafka topic {}", event.getId(), TOPIC);
            }catch (Exception e) {
                log.error("CRITICAL: Failed to relay event {} to Kafka. Transaction rolling back.", event.getId(), e);
                throw new RuntimeException("Kafka relay failed", e);
            }
        }
    }
}
