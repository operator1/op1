package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.IffReader;
import com.op1.iff.types.ID;
import com.op1.iff.types.SignedLong;
import com.op1.iff.types.UnsignedLong;
import com.op1.util.Check;

import java.io.IOException;

public class SoundDataChunk implements Chunk {

    private final ID chunkId = ChunkType.SOUND_DATA.getChunkId();

    private SignedLong chunkSize;
    private UnsignedLong offset;
    private UnsignedLong blockSize;

    // It is far more economical to store this data as bytes than in wrapper objects like SampleFrame and SamplePoint.
    // It does, however, expose a security hole in the getSampleData() method.
    private byte[] sampleData;

    private SoundDataChunk() {
    }

    private SoundDataChunk(SoundDataChunk chunk) {
        this.chunkSize = chunk.getChunkSize();
        this.offset = chunk.getOffset();
        this.blockSize = chunk.getBlockSize();
        this.sampleData = chunk.getSampleData();
    }

    public ID getChunkID() {
        return chunkId;
    }

    public SignedLong getChunkSize() {
        return chunkSize;
    }

    public UnsignedLong getOffset() {
        return offset;
    }

    public UnsignedLong getBlockSize() {
        return blockSize;
    }

    public byte[] getSampleData() {
        // TODO: Most of the references that gets passed around are immutable. This one isn't.
        return sampleData;
    }

    public static class Builder {

        private final SoundDataChunk instance;

        public Builder() {
            instance = new SoundDataChunk();
        }

        public SoundDataChunk build() {
            Check.notNull(instance.chunkId, "Missing chunkId");
            Check.notNull(instance.chunkSize, "Missing chunkSize");
            Check.notNull(instance.offset, "Missing offset");
            Check.notNull(instance.blockSize, "Missing blockSize");
            return new SoundDataChunk(instance);
        }

        public Builder withChunkSize(SignedLong chunkSize) {
            instance.chunkSize = chunkSize;
            return this;
        }

        public Builder withOffset(UnsignedLong offset) {
            instance.offset = offset;
            return this;
        }

        public Builder withBlockSize(UnsignedLong blockSize) {
            instance.blockSize = blockSize;
            return this;
        }

        public Builder withSampleData(byte[] sampleData) {
            instance.sampleData = sampleData;
            return this;
        }
    }

    public static SoundDataChunk readSoundDataChunk(IffReader iffReader) throws IOException {

        final SignedLong chunkSize = iffReader.readSignedLong();

        final SoundDataChunk.Builder builder = new SoundDataChunk.Builder()
                .withChunkSize(chunkSize)
                .withOffset(iffReader.readUnsignedLong())
                .withBlockSize(iffReader.readUnsignedLong());

        final int numSampleBytes = chunkSize.toInt() - 8;
        final byte[] sampleData = new byte[numSampleBytes];
        for (int i = 0; i < sampleData.length; i++) {
            sampleData[i] = iffReader.readByte();
        }
        return builder.withSampleData(sampleData).build();
    }

    public static class SoundDataChunkReader implements ChunkReader {
        public Chunk readChunk(IffReader reader) throws IOException {
            return SoundDataChunk.readSoundDataChunk(reader);
        }
    }
}
