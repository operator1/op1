package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.IffReader;
import com.op1.iff.types.*;
import com.op1.util.Check;

import java.io.IOException;

public class CommonChunk implements Chunk {

    private final ID chunkId = ChunkType.COMMON.getChunkId();   // 4 bytes
    private SignedLong chunkSize;                               // 4 bytes

    private SignedShort numChannels;                            // 2 bytes
    private UnsignedLong numSampleFrames;                       // 4 bytes
    private SignedShort sampleSize;                             // 2 bytes
    private Extended sampleRate;                                // 10 bytes

    // These two seem to be optional
    private ID codec;                                           // 4 bytes
    private byte[] description;                                 // the rest

    private CommonChunk() {
    }

    private CommonChunk(CommonChunk chunk) {
        this.chunkSize = DataTypes.copyOf(chunk.getChunkSize());
        this.numChannels = DataTypes.copyOf(chunk.getNumChannels());
        this.numSampleFrames = DataTypes.copyOf(chunk.getNumSampleFrames());
        this.sampleSize = DataTypes.copyOf(chunk.getSampleSize());
        this.sampleRate = DataTypes.copyOf(chunk.getSampleRate());
        if (chunk.getCodec() != null) {
            this.codec = DataTypes.copyOf(chunk.getCodec());
        }
        if (chunk.getDescription() != null) {
            this.description = DataTypes.copyOf(chunk.getDescription());
        }
    }

    public ID getChunkID() {
        return chunkId;
    }

    public SignedLong getChunkSize() {
        return chunkSize;
    }

    public SignedShort getNumChannels() {
        return numChannels;
    }

    public UnsignedLong getNumSampleFrames() {
        return numSampleFrames;
    }

    public SignedShort getSampleSize() {
        return sampleSize;
    }

    public Extended getSampleRate() {
        return sampleRate;
    }

    public ID getCodec() {
        return codec;
    }

    public byte[] getDescription() {
        return description;
    }

    public static class Builder {

        private final CommonChunk instance;

        public Builder() {
            instance = new CommonChunk();
        }

        public CommonChunk build() {
            Check.notNull(instance.chunkId, "Missing chunkId");
            Check.notNull(instance.chunkSize, "Missing chunkSize");
            Check.notNull(instance.numChannels, "Missing numChannels");
            Check.notNull(instance.numSampleFrames, "Missing numSampleFrames");
            Check.notNull(instance.sampleSize, "Missing sampleSize");
            Check.notNull(instance.sampleRate, "Missing sampleRate");
            return new CommonChunk(instance);
        }

        public Builder withChunkSize(SignedLong chunkSize) {
            instance.chunkSize = chunkSize;
            return this;
        }

        public Builder withNumChannels(SignedShort numChannels) {
            instance.numChannels = numChannels;
            return this;
        }

        public Builder withNumSampleFrames(UnsignedLong numSampleFrames) {
            instance.numSampleFrames = numSampleFrames;
            return this;
        }

        public Builder withSampleSize(SignedShort sampleSize) {
            instance.sampleSize = sampleSize;
            return this;
        }

        public Builder withSampleRate(Extended sampleRate) {
            instance.sampleRate = sampleRate;
            return this;
        }

        public Builder withCodec(ID codec) {
            instance.codec = codec;
            return this;
        }

        public Builder withDescription(byte[] description) {
            instance.description = description;
            return this;
        }
    }

    /**
     * It's assumed that the four bytes identifying the common chunk ("COMM") have been read and that the pointer is
     * pointing to the next byte.
     */
    public static CommonChunk readCommonChunk(IffReader reader) throws IOException {

        final SignedLong chunkSize = reader.readSignedLong();

        final CommonChunk.Builder builder = new CommonChunk.Builder()
                .withChunkSize(chunkSize)
                .withNumChannels(reader.readSignedShort())
                .withNumSampleFrames(reader.readUnsignedLong())
                .withSampleSize(reader.readSignedShort())
                .withSampleRate(reader.readExtended());

        if (chunkSize.toInt() == 18) {
            return builder.build();
        }

        return builder
                .withCodec(reader.readID())
                .withDescription(reader.readBytes(chunkSize.toInt() - 22))
                .build();
    }

    public static class CommonChunkReader implements ChunkReader {

        public Chunk readChunk(IffReader reader) throws IOException {
            return readCommonChunk(reader);
        }
    }

    @Override
    public String toString() {
        return "CommonChunk{" +
                "chunkId=" + chunkId +
                ", chunkSize=" + chunkSize +
                ", numChannels=" + numChannels +
                ", numSampleFrames=" + numSampleFrames +
                ", sampleSize=" + sampleSize +
                ", sampleRate=" + sampleRate +
                ", codec=" + codec +
                ", description=" + (description == null ? null : new String(description)) +
                '}';
    }
}
