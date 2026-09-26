package com.aegis.ledger.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TradeEventDto(
   UUID id,
   String externalReference,
   BigDecimal amount,
   String status
) {}
