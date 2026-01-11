package com.app.utils;

import org.apache.kafka.common.serialization.Serializer;
import org.springframework.batch.integration.chunk.ChunkRequest;
import org.springframework.util.SerializationUtils;
import org.springframework.stereotype.Component;

@Component
public class ChunkRequestSerializer implements Serializer<ChunkRequest<Object>> {
    @Override
    public byte[] serialize(String s, ChunkRequest<Object> chunkRequest) {
        if (chunkRequest == null) {
            return new byte[0];
        }
        return SerializationUtils.serialize(chunkRequest);
    }
}