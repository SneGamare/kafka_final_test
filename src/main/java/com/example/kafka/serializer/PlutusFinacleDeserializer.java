package com.example.kafka.serializer;

import com.example.kafka.model.PlutusFinacleData;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlutusFinacleDeserializer extends AvroDeserializer<PlutusFinacleData>{
    public PlutusFinacleDeserializer() {
        super(PlutusFinacleData.class);
      }
}
 