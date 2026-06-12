package com.example.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.Decoder;
import io.micronaut.serde.Deserializer;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Singleton
public class TicketCustomFieldDeserializer implements Deserializer<TicketCustomField> {

    @Override
    public TicketCustomField deserialize(@NonNull Decoder decoder, @NonNull DecoderContext context, @NonNull Argument<? super TicketCustomField> type) throws IOException {
        Object arbitrary = decoder.decodeArbitrary();
        if (!(arbitrary instanceof Map<?, ?> map)) {
            return null;
        }

        Object idObj = map.get("id");
        Long id = idObj instanceof Number n ? n.longValue() : null;
        Object value = map.get("value");

        if (value instanceof String s) {
            return new TicketCustomField.Text(id, s);
        } else if (value instanceof Boolean b) {
            return new TicketCustomField.Checkbox(id, b);
        } else if (value instanceof Integer i) {
            return new TicketCustomField.Numeric(id, i.longValue());
        } else if (value instanceof Long l) {
            return new TicketCustomField.Numeric(id, l);
        } else if (value instanceof Double d) {
            return new TicketCustomField.Decimal(id, d.floatValue());
        } else if (value instanceof Float f) {
            return new TicketCustomField.Decimal(id, f);
        } else if (value instanceof List<?> l) {
            // Assume TagList for lists
            @SuppressWarnings("unchecked")
            List<String> castedList = (List<String>) l;
            return new TicketCustomField.TagList(id, castedList);
        }

        return null;
    }
}
