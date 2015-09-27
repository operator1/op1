package com.op1.aiff;

import com.op1.iff.IffReader;
import com.op1.iff.types.ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class AiffReader implements Closeable {

    private final IffReader iffReader;

    private static final Logger LOGGER = LoggerFactory.getLogger(AiffReader.class);

    public AiffReader(IffReader iffReader) {
        this.iffReader = iffReader;
    }

    public static AiffReader newAiffReader(File file) throws FileNotFoundException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        final DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
        final IffReader iffReader = new IffReader(dataInputStream);
        return new AiffReader(iffReader);
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
                LOGGER.debug(String.format("Found chunk: %s", id.getName()));

                if (ChunkType.isKnownChunk(id.getName())) {
                    readKnownChunk(builder, id);

                } else {
                    builder.withChunk(id, UnknownChunk.readUnknownChunk(iffReader, id));
                }

            } catch (EOFException e) {
                // TODO: don't use exceptions to control expected flow.
                LOGGER.debug(String.format("Reached end of stream (%s)", e.getMessage()));
                break;
            }
        }
    }

    private void readKnownChunk(Aiff.Builder builder, ID id) throws IOException {
        final ChunkType chunkType = ChunkType.fromName(id.getName());
        builder.withChunk(id, chunkType.readChunk(iffReader));
    }

    public void close() throws IOException {
        iffReader.close();
    }
}
