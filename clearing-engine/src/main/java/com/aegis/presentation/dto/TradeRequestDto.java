package com.aegis.presentation.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
public record TradeRequestDto(
    @NotBlank(message = "External reference cannot be blank") String externalReference,
    @NotBlank(message = "Base currency cannot be blank") String baseCurrency,
    @NotBlank(message = "Quote currency cannot be blank") String quoteCurrency,
    @NotNull(message = "Amount is required") @Positive(message = "Amount must be strictly positive") BigDecimal amount
) {}
