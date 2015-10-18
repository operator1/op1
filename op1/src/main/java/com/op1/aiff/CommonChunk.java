package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.IffReader;
import com.op1.iff.types.*;
import com.op1.util.Check;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommonChunk implements Chunk {

    private final ID chunkId = ChunkType.COMMON.getChunkId();     // 4 bytes
    private SignedLong chunkSize;                                 // 4 bytes

    private SignedShort numChannels;                              // 2 bytes
    private UnsignedLong numSampleFrames;                         // 4 bytes
    private SignedShort sampleSize;                               // 2 bytes
    private Extended sampleRate;                                  // 10 bytes

    // codec and description are part of the AIFC spec
    private Optional<ID> codec = Optional.empty();                // 4 bytes
    private Optional<List<Byte>> description = Optional.empty();  // the rest

    @Override
    public int getPhysicalSize() {

        int size = chunkId.getSize()
                + chunkSize.getSize()
                + numChannels.getSize()
                + numSampleFrames.getSize()
                + sampleSize.getSize()
                + sampleRate.getSize();

        if (codec.isPresent()) {
            size += codec.get().getSize();
        }

        if (description.isPresent()) {
            size += description.get().size();
        }

        return size;
    }

    private CommonChunk() {
    }

    private CommonChunk(CommonChunk chunk) {
        this.chunkSize = DataTypes.copyOf(chunk.getChunkSize());
        this.numChannels = DataTypes.copyOf(chunk.getNumChannels());
        this.numSampleFrames = DataTypes.copyOf(chunk.getNumSampleFrames());
        this.sampleSize = DataTypes.copyOf(chunk.getSampleSize());
        this.sampleRate = DataTypes.copyOf(chunk.getSampleRate());

        if (chunk.codec.isPresent()) {
            this.codec = Optional.ofNullable(DataTypes.copyOf(chunk.getCodec()));
        }
        if (chunk.description.isPresent()) {
            final byte[] description = chunk.getDescription();
            final ArrayList<Byte> descriptionBytes = new ArrayList<>(description.length);
            for (Byte descriptionByte : description) {
                descriptionBytes.add(descriptionByte);
            }
            this.description = Optional.of(descriptionBytes);
        }
    }

    @Override
    public ID getChunkID() {
        return chunkId;
    }

    @Override
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
        return codec.orElse(null);
    }

    public byte[] getDescription() {
        if (description.isPresent()) {
            final byte[] bytes = new byte[description.get().size()];
            for (int i = 0; i < description.get().size(); i++) {
                bytes[i] = description.get().get(i);
            }
            return bytes;
        }
        return null;
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

            if (instance.description.isPresent()) {
                final List<Byte> bytes = instance.description.get();
                final boolean hasOddNumberOfBytes = bytes.size() % 2 == 1;
                if (hasOddNumberOfBytes) {
                    bytes.add((byte) 0);
                }
            }

            if (instance.description.isPresent() && !instance.codec.isPresent()) {
                throw new IllegalArgumentException("Expected codec to be present since description has been provided");
            }

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
            instance.codec = Optional.ofNullable(codec);
            return this;
        }

        public Builder withDescription(byte[] description) {
            if (description != null) {
                final List<Byte> bytes = new ArrayList<>(description.length);
                for (byte b : description) {
                    bytes.add(b);
                }
                instance.description = Optional.of(bytes);
            }
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
                ", description=" + (description.isPresent() ? new String(toByteArray(description.get())) : null) +
                '}';
    }

    private static byte[] toByteArray(List<Byte> byteList) {
        final byte[] bytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }
        return bytes;
    }
}
