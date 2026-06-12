package com.example.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.Decoder;
import io.micronaut.serde.Encoder;
import io.micronaut.serde.Serde;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class TicketCustomFieldSerde<T> implements Serde<TicketCustomField<T>> {

    private static final String VALIDATION_ERR = "Invalid value type for custom field";

    @Override
    @SuppressWarnings("unchecked")
    public TicketCustomField<T> deserialize(@NonNull Decoder decoder, @NonNull DecoderContext context, @NonNull Argument<? super TicketCustomField<T>> instance) throws IOException {
        Long id = null;
        Object value = null;
        Argument<?> valueTypeArgument = instance.getFirstTypeVariable().orElse(Argument.OBJECT_ARGUMENT);
        
        var objectDecoder = decoder.decodeObject();
        String currentKey;
        while ((currentKey = objectDecoder.decodeKey()) != null) {
            switch (currentKey) {
                case "id" -> id = objectDecoder.decodeLong();
                case "value" -> value = deserializeValue(objectDecoder, context, valueTypeArgument);
                default -> objectDecoder.skipValue();
            }
        }

        return new TicketCustomField<T>().setId(id).setValue((T) value);
    }

    private Object deserializeValue(Decoder decoder, DecoderContext context, Argument<?> type) throws IOException {
        Class<?> javaType = type.getType();
        return switch (javaType) {
            case Class<?> c when c == String.class -> decoder.decodeString();
            case Class<?> c when c == Boolean.class -> decoder.decodeBoolean();
            case Class<?> c when c == Long.class -> decoder.decodeLong();
            case Class<?> c when c == Float.class -> decoder.decodeFloat();
            case Class<?> c when c == List.class -> decodeStringList(decoder, context);
            case Class<?> c when c == Object.class || c == Void.class -> {
                Object arbitrary = decoder.decodeArbitrary();
                if (arbitrary instanceof Integer i) yield i.longValue();
                if (arbitrary instanceof Double d) yield d.floatValue();
                yield arbitrary;
            }
            default -> throw new IllegalArgumentException(VALIDATION_ERR + ": " + javaType);
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<?> decodeStringList(Decoder decoder, DecoderContext context) throws IOException {
        Argument<List<String>> arg = (Argument) Argument.of(List.class, String.class);
        return context.findDeserializer(arg).deserialize(decoder, context, arg);
    }

    @Override
    public void serialize(@NonNull Encoder encoder, @NonNull EncoderContext context, @NonNull Argument<? extends TicketCustomField<T>> type, @NonNull TicketCustomField<T> value) throws IOException {
        Encoder objectEncoder = encoder.encodeObject(type);
        objectEncoder.encodeKey("id");
        if (value.getId() == null) {
            objectEncoder.encodeNull();
        } else {
            objectEncoder.encodeLong(value.getId());
        }
        
        objectEncoder.encodeKey("value");
        T v = value.getValue();
        if (v == null) {
            objectEncoder.encodeNull();
        } else {
            serializeValue(objectEncoder, context, v);
        }
    }

    @SuppressWarnings("unchecked")
    private void serializeValue(Encoder encoder, EncoderContext context, Object v) throws IOException {
        switch (v) {
            case String s -> encoder.encodeString(s);
            case Long l -> encoder.encodeLong(l);
            case Integer i -> encoder.encodeLong(i.longValue());
            case Boolean b -> encoder.encodeBoolean(b);
            case Float f -> encoder.encodeFloat(f);
            case Double d -> encoder.encodeDouble(d);
            case List<?> list -> {
                Encoder arrayEncoder = encoder.encodeArray(Argument.of(List.class, Object.class));
                for (Object item : list) {
                    if (item == null) {
                        arrayEncoder.encodeNull();
                    } else {
                        serializeValue(arrayEncoder, context, item);
                    }
                }
            }
            case null -> encoder.encodeNull();
            default -> {
                Class<?> clazz = v.getClass();
                Argument<Object> arg = (Argument<Object>) Argument.of(clazz);
                context.findSerializer(arg).serialize(encoder, context, arg, v);
            }
        }
    }
}
