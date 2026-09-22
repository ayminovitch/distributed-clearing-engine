package com.aegis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AegisApplication {

    private static final Logger log = LoggerFactory.getLogger(AegisApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AegisApplication.class, args);
        log.info("Aegis Distributed Reconciliation & Clearing Engine has started successfully.");
    }
}