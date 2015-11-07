package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.IffReader;
import com.op1.iff.types.ID;
import com.op1.iff.types.SignedLong;
import com.op1.iff.types.UnsignedLong;
import com.op1.util.Check;

import java.io.IOException;
import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SoundDataChunk that = (SoundDataChunk) o;

        if (chunkId != null ? !chunkId.equals(that.chunkId) : that.chunkId != null) return false;
        if (chunkSize != null ? !chunkSize.equals(that.chunkSize) : that.chunkSize != null) return false;
        if (offset != null ? !offset.equals(that.offset) : that.offset != null) return false;
        if (blockSize != null ? !blockSize.equals(that.blockSize) : that.blockSize != null) return false;
        return Arrays.equals(sampleData, that.sampleData);

    }

    @Override
    public int hashCode() {
        int result = chunkId != null ? chunkId.hashCode() : 0;
        result = 31 * result + (chunkSize != null ? chunkSize.hashCode() : 0);
        result = 31 * result + (offset != null ? offset.hashCode() : 0);
        result = 31 * result + (blockSize != null ? blockSize.hashCode() : 0);
        result = 31 * result + (sampleData != null ? Arrays.hashCode(sampleData) : 0);
        return result;
    }

    @Override
    public int getPhysicalSize() {
        return chunkId.getSize()
                + chunkSize.getSize()
                + offset.getSize()
                + blockSize.getSize()
                + sampleData.length;
    }

    @Override
    public String toString() {
        return "SoundDataChunk{" +
                "chunkId=" + chunkId +
                ", chunkSize=" + chunkSize +
                ", offset=" + offset +
                ", blockSize=" + blockSize +
                ", sampleData is " + sampleData.length + " bytes" +
                '}';
    }

    @Override
    public ID getChunkID() {
        return chunkId;
    }

    @Override
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

        final int numSampleBytes = chunkSize.toInt() - 8; // -8 for offset and block size
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
