package com.example.kafka.serializer;

import java.io.ByteArrayInputStream;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.commons.codec.binary.Hex;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
public class AvroDeserializer<T> implements Deserializer<T> {

    private Class<T> targetType;

    public AvroDeserializer() {
        // Default constructor required by Kafka
    }

    public AvroDeserializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        if (targetType == null) {
            String targetTypeName = (String) configs.get("target.type");
            try {
                targetType = (Class<T>) Class.forName(targetTypeName);
            } catch (ClassNotFoundException e) {
                throw new SerializationException("Failed to initialize AvroDeserializer", e);
            }
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            DatumReader<T> datumReader = new SpecificDatumReader<>(targetType);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(inputStream, null);
            T deserializedData = datumReader.read(null, decoder);
            return deserializedData;
        } catch (Exception e) {
            log.warn("Failed to deserialize event: {}", e.getMessage());
            throw new SerializationException("Error when deserializing byte[] to " + targetType.getName(), e);
        }
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        String eventString = new String(Hex.encodeHex(data));
        log.debug("AvroDeserializer:deserialize received encodeHex {}", eventString);
        try {
            return deserialize(topic, data);
        } catch (SerializationException e) {
            log.warn("Failed to deserialize event: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void close() {
        // Nothing to close
    }
} 