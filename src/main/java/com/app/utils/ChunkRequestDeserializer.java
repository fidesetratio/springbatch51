package com.app.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.batch.integration.chunk.ChunkRequest;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.stereotype.Component;

@Component
public class ChunkRequestDeserializer implements Deserializer<ChunkRequest<Object>> {
    @Override
    public ChunkRequest deserialize(String s, byte[] bytes) {
        try {
            return (ChunkRequest) new DefaultDeserializer().deserialize(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}