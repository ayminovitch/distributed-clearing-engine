package com.aegis.infrastructure.messaging;

import com.aegis.domain.OutboxEvent;
import com.aegis.infrastructure.persistence.OutboxEventRepository;
import java.util.List;
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
            kafkaTemplate.send(TOPIC, event.getAggregateId(), event.getPayload());
            event.markAsProcessed();
            outboxEventRepository.save(event);

            log.info("Event with ID {} has been relayed to Kafka topic {}", event.getId(), TOPIC);
        }
    }
}
