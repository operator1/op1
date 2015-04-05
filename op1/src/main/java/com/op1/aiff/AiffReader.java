package com.op1.aiff;

import com.op1.iff.IffReader;
import com.op1.iff.types.ID;

import java.io.IOException;

public class AiffReader {

    private final IffReader iffReader;

    public AiffReader(IffReader iffReader) {
        this.iffReader = iffReader;
    }

    public Aiff readAiff() throws IOException {

        final Aiff.Builder builder = new Aiff.Builder()
                .withChunkId(iffReader.readID())
                .withChunkSize(iffReader.readSignedLong())
                .withFormType(iffReader.readID());

        readChunks(builder);
        return builder.build();
    }

    private void readChunks(Aiff.Builder builder) throws IOException {

        while (true) {
            try {
                final ID id = iffReader.readID();
                System.out.println(String.format("Found chunk: %s", id.getName()));

                if (ChunkType.isKnownChunk(id.getName())) {
                    readKnownChunk(builder, id);

                } else {
                    builder.withChunk(id, UnknownChunk.readUnknownChunk(iffReader, id));
                }

            } catch (Exception e) {
                // TODO: don't use exceptions to control expected flow.
                System.out.println("Reached end of stream");
                break;
            }
        }
    }

    private void readKnownChunk(Aiff.Builder builder, ID id) throws IOException {
        final ChunkType chunkType = ChunkType.fromName(id.getName());
        builder.withChunk(id, chunkType.readChunk(iffReader));
    }
}
