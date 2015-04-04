package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.types.ID;
import com.op1.iff.types.SignedLong;
import com.op1.util.Check;

import java.util.LinkedHashMap;
import java.util.Map;

public class Aiff implements Chunk {

    private ID chunkId;
    private SignedLong chunkSize;
    private ID format;

    private Map<ID, Chunk> chunks = new LinkedHashMap<ID, Chunk>();

    public Aiff() {
    }

    @Override
    public ID getChunkID() {
        return chunkId;
    }

    public SignedLong getChunkSize() {
        return chunkSize;
    }

    public ID getFormat() {
        return format;
    }

    public Map<ID, Chunk> getChunks() {
        return chunks;
    }

    public CommonChunk getCommonChunk() {
        return (CommonChunk) chunks.get(ChunkType.COMMON.getId());
    }

    public SoundDataChunk getSoundDataChunk() {
        return (SoundDataChunk) chunks.get(ChunkType.SOUND_DATA.getId());
    }

    public static class Builder {

        private final Aiff instance;

        public Builder() {
            this.instance = new Aiff();
        }

        public Aiff build() {
            Check.notNull(instance.chunkId, "Missing chunkId");
            Check.notNull(instance.chunkSize, "Missing chunkSize");
            Check.that(instance.chunks.containsKey(ChunkType.COMMON.getId()), "Missing common chunk");
            // TODO: Sound data chunk is only required if there are >0 samples.
            Check.that(instance.chunks.containsKey(ChunkType.SOUND_DATA.getId()), "Missing sound data chunk");
            return instance;
        }

        public Builder withChunkId(ID chunkId) {
            instance.chunkId = chunkId;
            return this;
        }

        public Builder withChunkSize(SignedLong chunkSize) {
            instance.chunkSize = chunkSize;
            return this;
        }

        public Builder withFormat(ID format) {
            instance.format = format;
            return this;
        }

        public Builder withChunk(ID chunkId, Chunk chunk) {
            instance.chunks.put(chunkId, chunk);
            return this;
        }
    }
}
