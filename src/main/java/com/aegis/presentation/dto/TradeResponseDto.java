package com.aegis.presentation.dto;
import java.util.UUID;
public record TradeResponseDto(UUID tradeId, String status, String message) {}
