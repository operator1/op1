package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.IffReader;
import com.op1.iff.types.ID;
import com.op1.iff.types.SignedLong;
import com.op1.util.Check;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class UnknownChunk implements Chunk {

    private ID chunkId;
    private SignedLong chunkSize;
    private byte[] chunkData;

    private static final Logger LOGGER = LoggerFactory.getLogger(UnknownChunk.class);

    private UnknownChunk() {
    }

    private UnknownChunk(UnknownChunk chunk) {
        this.chunkId = chunk.getChunkID();
        this.chunkSize = chunk.getChunkSize();
        this.chunkData = Arrays.copyOf(chunk.getChunkData(), chunk.getChunkData().length);
    }

    @Override
    public int getPhysicalSize() {
        return chunkId.getSize()
                + chunkSize.getSize()
                + chunkData.length;
    }

    @Override
    public ID getChunkID() {
        return chunkId;
    }

    @Override
    public SignedLong getChunkSize() {
        return chunkSize;
    }

    public byte[] getChunkData() {
        return chunkData;
    }

    public static class Builder {

        private final UnknownChunk instance;

        public Builder() {
            instance = new UnknownChunk();
        }

        public UnknownChunk build() {
            Check.notNull(instance.chunkId, "Missing chunkId");
            Check.notNull(instance.chunkSize, "Missing chunkSize");
            Check.notNull(instance.chunkData, "Missing chunkData");
            return new UnknownChunk(instance);
        }

        public Builder withChunkId(ID chunkId) {
            instance.chunkId = chunkId;
            return this;
        }

        public Builder withChunkSize(SignedLong chunkSize) {
            instance.chunkSize = chunkSize;
            return this;
        }

        public Builder withChunkData(byte[] chunkData) {
            instance.chunkData = chunkData;
            return this;
        }
    }

    public static UnknownChunk readUnknownChunk(IffReader iffReader, ID chunkId) throws IOException {
        final SignedLong chunkSize = iffReader.readSignedLong();
        LOGGER.debug(String.format("Chunk size: %s", chunkSize));
        final byte[] chunkData = iffReader.readBytes(chunkSize.toInt());
        return new UnknownChunk.Builder()
                .withChunkId(chunkId)
                .withChunkSize(chunkSize)
                .withChunkData(chunkData)
                .build();
    }
}
