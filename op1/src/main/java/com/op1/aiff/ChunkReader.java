package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.IffReader;

import java.io.IOException;

public interface ChunkReader {
    Chunk readChunk(IffReader reader) throws IOException;
}
