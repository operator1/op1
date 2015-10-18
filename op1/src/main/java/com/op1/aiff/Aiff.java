package com.op1.aiff;

import com.op1.drumkit.DrumkitMeta;
import com.op1.iff.Chunk;
import com.op1.iff.types.ID;
import com.op1.iff.types.SignedChar;
import com.op1.iff.types.SignedLong;
import com.op1.util.Check;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.op1.aiff.ChunkType.*;
import static java.lang.System.lineSeparator;

public class Aiff implements Chunk {

    private ID chunkId;
    private SignedLong chunkSize;
    private ID formType;

    private Map<ID, List<Chunk>> chunksMap = new LinkedHashMap<>();

    private Aiff() {
    }

    public ID getChunkID() {
        return chunkId;
    }

    @Override
    public int getPhysicalSize() {

        int size = chunkId.getSize()
                + chunkSize.getSize()
                + formType.getSize();

        for (List<Chunk> chunks : chunksMap.values()) {
            for (Chunk chunk : chunks) {
                size += chunk.getPhysicalSize();
            }
        }

        return size;
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

    public boolean hasChunk(ChunkType chunkType) {
        return chunksMap.containsKey(chunkType.getChunkId());
    }

    public Chunk getChunk(ID chunkId) {
        final List<Chunk> chunks = chunksMap.get(chunkId);
        Check.that(chunks != null && chunks.size() == 1, String.format("There are more than one %s chunk", chunkId));
        return chunks.get(0);
    }

    public CommonChunk getCommonChunk() {
        return (CommonChunk) chunksMap.get(ChunkType.COMMON.getChunkId()).get(0);
    }

    public boolean hasCommonChunk() {
        return chunksMap.containsKey(ChunkType.COMMON.getChunkId());
    }

    /**
     * Returns the Sound Data chunk or throws an IllegalStateException if there isn't one.
     * Use hasSoundDataChunk() to find out whether the chunk is available before calling this method.
     */
    public SoundDataChunk getSoundDataChunk() {
        if (hasSoundDataChunk()) {
            return (SoundDataChunk) chunksMap.get(SOUND_DATA.getChunkId()).get(0);
        }
        throw new IllegalStateException("There is no SoundDataChunk");
    }

    public boolean hasSoundDataChunk() {
        return chunksMap.containsKey(SOUND_DATA.getChunkId());
    }

    public static class Builder {

        private final Aiff instance;

        public Builder() {
            this.instance = new Aiff();
        }

        public Aiff build() {
            Check.notNull(instance.chunkId, "Missing chunkId");
            Check.notNull(instance.chunkSize, "Missing chunkSize");
            Check.state(hasExactlyOneCommonChunk(), "Must have exactly one common chunk");
            if (instance.getCommonChunk().getNumSampleFrames().toLong() > 0) {
                Check.state(instance.hasSoundDataChunk(), "Missing sound data chunk");
            }
            // Note: we permit a superfluous sound data chunk when numSampleFrames is 0.
            Check.state(hasZeroOrOneSoundDataChunks(), "Can have at most one sound data chunk");
            return instance;
        }

        private boolean hasZeroOrOneSoundDataChunks() {
            return !instance.hasSoundDataChunk() || instance.chunksMap.get(SOUND_DATA.getChunkId()).size() == 1;
        }

        private boolean hasExactlyOneCommonChunk() {
            return instance.hasCommonChunk() && instance.chunksMap.get(COMMON.getChunkId()).size() == 1;
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
                chunks = new ArrayList<>();
                instance.chunksMap.put(chunkId, chunks);
            }
            chunks.add(chunk);
            return this;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append(lineSeparator());
        builder.append("----------------------------------------").append(lineSeparator());
        builder.append("| chunkId: ").append(chunkId).append(lineSeparator());
        builder.append("| chunkSize: ").append(chunkSize).append(lineSeparator());
        builder.append("|").append(lineSeparator());
        builder.append("| formType: ").append(formType).append(lineSeparator());
        builder.append("| |-------------------------------------").append(lineSeparator());

        for (List<Chunk> chunkList : chunksMap.values()) {
            for (Chunk chunk : chunkList) {
                if (chunk instanceof CommonChunk) {
                    CommonChunk commonChunk = (CommonChunk) chunk;
                    toString(builder, commonChunk);
                } else if (chunk instanceof SoundDataChunk) {
                    SoundDataChunk soundDataChunk = (SoundDataChunk) chunk;
                    toString(builder, soundDataChunk);
                } else if (chunk instanceof FormatVersionChunk) {
                    FormatVersionChunk formatVersionChunk = (FormatVersionChunk) chunk;
                    toString(builder, formatVersionChunk);
                } else if (chunk instanceof ApplicationChunk) {
                    ApplicationChunk applicationChunk = (ApplicationChunk) chunk;
                    toString(builder, applicationChunk);
                }
            }
        }

        builder.append("|").append(lineSeparator());
        builder.append("----------------------------------------").append(lineSeparator());

        if (hasChunk(APPLICATION)) {
            final List<Chunk> chunks = chunksMap.get(APPLICATION.getChunkId());
            for (Chunk chunk : chunks) {
                final String jsonPrettyPrint = DrumkitMeta.toJsonPrettyPrint(DrumkitMeta.fromJson(SignedChar.getString(((ApplicationChunk) chunk).getData())));
                builder.append(String.format("%n%s", jsonPrettyPrint));
            }
        }

        return builder.toString();
    }

    private void toString(StringBuilder builder, CommonChunk commonChunk) {
        builder.append("| | chunkId: ").append(commonChunk.getChunkID()).append(lineSeparator());
        builder.append("| | chunkSize: ").append(commonChunk.getChunkSize()).append(lineSeparator());
        builder.append("| |").append(lineSeparator());
        builder.append("| | numChannels: ").append(commonChunk.getNumChannels()).append(lineSeparator());
        builder.append("| | numSampleFrames: ").append(commonChunk.getNumSampleFrames()).append(lineSeparator());
        builder.append("| | sampleSize: ").append(commonChunk.getSampleSize()).append(lineSeparator());
        builder.append("| | sampleRate: ").append(commonChunk.getSampleRate()).append(lineSeparator());
        if (commonChunk.getCodec() != null) {
            builder.append("| | codec: ").append(commonChunk.getCodec()).append(lineSeparator());
        }
        if (commonChunk.getDescription() != null) {
            builder.append("| | description: ").append(new String(commonChunk.getDescription())).append(lineSeparator());
        }
        builder.append("| |-------------------------------------").append(lineSeparator());
    }

    private void toString(StringBuilder builder, SoundDataChunk chunk) {
        builder.append("| | chunkId: ").append(chunk.getChunkID()).append(lineSeparator());
        builder.append("| | chunkSize: ").append(chunk.getChunkSize()).append(lineSeparator());
        builder.append("| |").append(lineSeparator());
        builder.append("| | offset: ").append(chunk.getOffset()).append(lineSeparator());
        builder.append("| | blockSize: ").append(chunk.getBlockSize()).append(lineSeparator());
        builder.append("| | sampleData: ").append("<sample data> (").append(chunk.getSampleData().length).append(" bytes)").append(lineSeparator());
        builder.append("| |-------------------------------------").append(lineSeparator());
    }

    private void toString(StringBuilder builder, FormatVersionChunk chunk) {
        builder.append("| | chunkId: ").append(chunk.getChunkID()).append(lineSeparator());
        builder.append("| | chunkSize: ").append(chunk.getChunkSize()).append(lineSeparator());
        builder.append("| |").append(lineSeparator());
        builder.append("| | timestamp: ").append(chunk.getTimestamp()).append(lineSeparator());
        builder.append("| |-------------------------------------").append(lineSeparator());
    }

    private void toString(StringBuilder builder, ApplicationChunk chunk) {
        builder.append("| | chunkId: ").append(chunk.getChunkID()).append(lineSeparator());
        builder.append("| | chunkSize: ").append(chunk.getChunkSize()).append(lineSeparator());
        builder.append("| |").append(lineSeparator());
        builder.append("| | applicationSignature: ").append(chunk.getApplicationSignature()).append(lineSeparator());
        builder.append("| | data: ").append("<data> (").append(chunk.getData().length).append(" bytes)").append(lineSeparator());
        builder.append("| |-------------------------------------").append(lineSeparator());
    }
}
