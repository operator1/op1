package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.types.ID;
import com.op1.iff.types.SignedLong;
import com.op1.util.Check;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Aiff implements Chunk {

    private ID chunkId;
    private SignedLong chunkSize;
    private ID formType;

    private Map<ID, List<Chunk>> chunksMap = new LinkedHashMap<ID, List<Chunk>>();

    public Aiff() {
    }

    @Override
    public ID getChunkID() {
        return chunkId;
    }

    public SignedLong getChunkSize() {
        return chunkSize;
    }

    public ID getFormType() {
        return formType;
    }

    public Map<ID, List<Chunk>> getChunksMap() {
        return chunksMap;
    }

    public List<Chunk> getChunks(ID chunkId) {
        return chunksMap.get(chunkId);
    }

    public CommonChunk getCommonChunk() {
        return (CommonChunk) chunksMap.get(ChunkType.COMMON.getChunkId()).get(0);
    }

    /**
     * Returns the Sound Data chunk or throws an IllegalStateException if there isn't one.
     * Use hasSoundDataChunk() to find out whether the chunk is available before calling this method.
     */
    public SoundDataChunk getSoundDataChunk() {
        if (hasSoundDataChunk()) {
            return (SoundDataChunk) chunksMap.get(ChunkType.SOUND_DATA.getChunkId()).get(0);
        }
        throw new IllegalStateException("There is no SoundDataChunk");
    }

    public boolean hasSoundDataChunk() {
        return chunksMap.containsKey(ChunkType.SOUND_DATA.getChunkId());
    }

    public static class Builder {

        private final Aiff instance;

        public Builder() {
            this.instance = new Aiff();
        }

        public Aiff build() {
            Check.notNull(instance.chunkId, "Missing chunkId");
            Check.notNull(instance.chunkSize, "Missing chunkSize");
            Check.that(instance.chunksMap.containsKey(ChunkType.COMMON.getChunkId()), "Missing common chunk");
            Check.state(instance.chunksMap.get(ChunkType.COMMON.getChunkId()).size() == 1, "Must have exactly one common chunk");
            // TODO: Sound data chunk is only required if there are >0 samples.
            Check.that(instance.chunksMap.containsKey(ChunkType.SOUND_DATA.getChunkId()), "Missing sound data chunk");
            Check.state(instance.chunksMap.get(ChunkType.SOUND_DATA.getChunkId()).size() <= 1, "Can have at most one sound data chunk");
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

        public Builder withFormType(ID format) {
            instance.formType = format;
            return this;
        }

        public Builder withChunk(ID chunkId, Chunk chunk) {
            // Guava's multimap would come in handy here, but resisting for the sake of having no dependencies.
            List<Chunk> chunks = instance.chunksMap.get(chunkId);
            if (chunks == null) {
                chunks = new ArrayList<Chunk>();
                instance.chunksMap.put(chunkId, chunks);
            }
            chunks.add(chunk);
            return this;
        }
    }
}
