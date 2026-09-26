package com.aegis;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AegisApplication {

    private static final Logger log = LoggerFactory.getLogger(AegisApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AegisApplication.class, args);
        log.info("Aegis Distributed Reconciliation & Clearing Engine has started successfully.");
    }

    @Bean
    public NewTopic tradeEventsTopic() {
        return TopicBuilder.name("aegis.trades.events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}