package com.aegis.ledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LedgerApplication {
    public static final Logger log = LoggerFactory.getLogger(LedgerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LedgerApplication.class, args);
        log.info("Ledger service started on port 8001.");
    }
}
