package com.example.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TicketCustomField<T> {
    private Long id;
    private T value;
}
