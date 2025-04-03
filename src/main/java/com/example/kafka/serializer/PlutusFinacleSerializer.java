package com.example.kafka.serializer;

import com.example.kafka.model.PlutusFinacleData;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlutusFinacleSerializer implements Serializer<PlutusFinacleData> {
    private static final Logger logger = LoggerFactory.getLogger(PlutusFinacleSerializer.class);
    private final CbsAvroSerializer<PlutusFinacleData> delegate = new CbsAvroSerializer<>();

    @Override
    public byte[] serialize(String topic, PlutusFinacleData data) {
        logger.debug("Serializing PlutusFinacleData for topic: {}", topic);
        return delegate.serialize(topic, data);
    }
} 