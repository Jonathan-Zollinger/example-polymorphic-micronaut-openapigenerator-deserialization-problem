package com.example.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.Decoder;
import io.micronaut.serde.Deserializer;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.List;

@Singleton
public class SealedTicketCustomFieldDeserializer implements Deserializer<TicketCustomField> {

    @Override
    public TicketCustomField deserialize(@NonNull Decoder decoder, @NonNull DecoderContext context, @NonNull Argument<? super TicketCustomField> type) throws IOException {

        Long id = null;
        Object value = null;

        // 1. Gather raw data using streaming decoder
        try (Decoder objectDecoder = decoder.decodeObject()) {
            String key;
            while ((key = objectDecoder.decodeKey()) != null) {
                switch (key) {
                    case "id" -> id = objectDecoder.decodeLong();
                    case "value" -> value = objectDecoder.decodeArbitrary();
                    default -> objectDecoder.skipValue();
                }
            }
        }

        if (value == null) return null;

        if (value instanceof List<?> l) {
            @SuppressWarnings("unchecked") List<String> castedList = (List<String>) l;
            return new TicketCustomField.TagList(id, castedList);
        }

        return switch (value) {
            case String s -> new TicketCustomField.Text(id, s);
            case Boolean b -> new TicketCustomField.Checkbox(id, b);
            case Number n -> handleNumericType(id, n);
            default -> null;
        };
    }

    private TicketCustomField handleNumericType(Long id, Number n) {
        if (n instanceof Double || n instanceof Float) {
            return new TicketCustomField.Decimal(id, n.floatValue());
        }
        return new TicketCustomField.Numeric(id, n.longValue());
    }
}
