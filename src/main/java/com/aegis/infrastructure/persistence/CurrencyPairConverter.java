package com.aegis.infrastructure.persistence;

import com.aegis.domain.CurrencyPair;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CurrencyPairConverter implements AttributeConverter<CurrencyPair, String> {

    @Override
    public String convertToDatabaseColumn(CurrencyPair attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.baseCurrency() + "/" + attribute.quoteCurrency();
    }

    @Override
    public CurrencyPair convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] parts = dbData.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid currency pair format in database: " + dbData);
        }

        return new CurrencyPair(parts[0], parts[1]);
    }
}
