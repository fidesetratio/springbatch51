package com.app.utils;

import org.apache.kafka.common.serialization.Serializer;
import org.springframework.batch.integration.chunk.ChunkResponse;
import org.springframework.util.SerializationUtils;
import org.springframework.stereotype.Component;

@Component
public class ChunkResponseSerializer implements Serializer<ChunkResponse> {
    @Override
    public byte[] serialize(String s, ChunkResponse chunkResponse) {
        return SerializationUtils.serialize(chunkResponse);
    }
}
