package com.aegis.domain;

import org.hibernate.annotations.DialectOverride;

public record CurrencyPair(String baseCurrency, String quoteCurrency) {
    public CurrencyPair{
        if (baseCurrency == null || baseCurrency.length() != 3) {
            throw new IllegalArgumentException("Base currency must be a 3-letter ISO code");
        }

        if (quoteCurrency == null || quoteCurrency.length() != 3) {
            throw new IllegalArgumentException("Quote currency must be a 3-letter ISO code");
        }

        baseCurrency = baseCurrency.toUpperCase();
        quoteCurrency = quoteCurrency.toUpperCase();
    }

    @Override
    public String toString() {
        return baseCurrency + "/" + quoteCurrency;
    }
}
