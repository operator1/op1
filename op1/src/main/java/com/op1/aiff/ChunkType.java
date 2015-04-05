package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.IffReader;
import com.op1.iff.types.ID;
import com.op1.util.Check;

import java.io.IOException;

public enum ChunkType {

    COMMON("COMM", new CommonChunk.CommonChunkReader()),
    SOUND_DATA("SSND", new SoundDataChunk.SoundDataChunkReader()),
    MARKER("MARK", new MarkerChunk.MarkerChunkReader()),
    INSTRUMENT("INST", new InstrumentChunk.InstrumentChunkReader()),
    APPLICATION("APPL", new ApplicationChunk.ApplicationChunkReader());

    // Here are some currently unsupported chunks that need fleshing out:

    //COMMENT("COMT"),
    //NAME("NAME"),
    //AUTHOR("AUTH"),
    //COPYRIGHT("(c) "),
    //ANNOTATION("ANNO"),
    //AUDIO_RECORDING("AESD"),
    //MIDI_DATA("MIDI");

    private final String name;
    private final ID id;
    private final ChunkReader chunkReader;

    ChunkType(String name, ChunkReader chunkReader) {
        this.name = name;
        this.id = new ID(name.getBytes()); // TODO: charset
        this.chunkReader = chunkReader;
    }

    public ID getChunkId() {
        return id;
    }

    public Chunk readChunk(IffReader reader) throws IOException {
        return chunkReader.readChunk(reader);
    }

    public static ChunkType fromName(String name) {
        for (ChunkType chunkType : ChunkType.values()) {
            if (chunkType.name.equals(name)) {
                return chunkType;
            }
        }
        throw new IllegalArgumentException("Unrecognized chunk");
    }

    public static boolean isKnownChunk(String name) {
        for (ChunkType chunkType : ChunkType.values()) {
            if (chunkType.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isChunkType(Chunk chunk, ChunkType chunkType) {
        Check.notNull(chunk, "Chunk is null");
        Check.notNull(chunkType, "ChunkType is null");
        return chunk.getChunkID().equals(chunkType.getChunkId());
    }
}
