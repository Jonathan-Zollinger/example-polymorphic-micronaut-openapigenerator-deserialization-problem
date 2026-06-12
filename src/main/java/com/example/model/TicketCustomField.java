package com.example.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
@Introspected
public sealed interface TicketCustomField 
    permits TicketCustomField.Text, 
            TicketCustomField.Numeric, 
            TicketCustomField.Decimal, 
            TicketCustomField.Checkbox, 
            TicketCustomField.TagList {

    Long id();
    Object value();

    @Serdeable record Text(Long id, String value) implements TicketCustomField {}
    @Serdeable record Numeric(Long id, Long value) implements TicketCustomField {}
    @Serdeable record Decimal(Long id, Float value) implements TicketCustomField {}
    @Serdeable record Checkbox(Long id, Boolean value) implements TicketCustomField {}
    @Serdeable record TagList(Long id, List<String> value) implements TicketCustomField {}
}
