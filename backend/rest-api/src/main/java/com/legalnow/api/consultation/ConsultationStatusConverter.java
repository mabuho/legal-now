package com.legalnow.api.consultation;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ConsultationStatusConverter implements AttributeConverter<ConsultationStatus, String> {

    @Override
    public String convertToDatabaseColumn(ConsultationStatus attribute) {
        return attribute == null ? null : attribute.toDb();
    }

    @Override
    public ConsultationStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ConsultationStatus.fromDb(dbData);
    }
}
