package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.IffReader;
import com.op1.iff.types.ID;
import com.op1.iff.types.SignedLong;
import com.op1.iff.types.UnsignedLong;
import com.op1.util.Check;

import java.io.IOException;

public class FormatVersionChunk implements Chunk {

    private final ID chunkId = ChunkType.FORMAT_VERSION.getChunkId();  // 4 bytes
    private SignedLong chunkSize;                                      // 4 bytes
    private UnsignedLong timestamp;                                    // 4 bytes

    private FormatVersionChunk() {
    }

    private FormatVersionChunk(FormatVersionChunk chunk) {
        this.chunkSize = chunk.getChunkSize();
        this.timestamp = chunk.getTimestamp();
    }

    @Override
    public String toString() {
        return "FormatVersionChunk{" +
                "chunkId=" + chunkId +
                ", chunkSize=" + chunkSize +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public int getPhysicalSize() {
        return chunkId.getSize()
                + chunkSize.getSize()
                + timestamp.getSize();
    }

    @Override
    public ID getChunkID() {
        return chunkId;
    }

    @Override
    public SignedLong getChunkSize() {
        return chunkSize;
    }

    public UnsignedLong getTimestamp() {
        return timestamp;
    }

    public static class Builder {

        private final FormatVersionChunk instance;

        public Builder() {
            instance = new FormatVersionChunk();
        }

        public FormatVersionChunk build() {
            Check.notNull(instance.chunkSize, "Missing chunkSize");
            Check.notNull(instance.timestamp, "Missing timestamp");
            return new FormatVersionChunk(instance);
        }

        public Builder withChunkSize(SignedLong chunkSize) {
            instance.chunkSize = chunkSize;
            return this;
        }

        public Builder withTimestamp(UnsignedLong timestamp) {
            instance.timestamp = timestamp;
            return this;
        }
    }

    public static FormatVersionChunk readFormatVersionChunk(IffReader reader) throws IOException {
        return new FormatVersionChunk.Builder()
                .withChunkSize(reader.readSignedLong())
                .withTimestamp(reader.readUnsignedLong())
                .build();
    }

    public static class FormatVersionChunkReader implements ChunkReader {
        public Chunk readChunk(IffReader reader) throws IOException {
            return readFormatVersionChunk(reader);
        }
    }
}
