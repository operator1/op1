package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.IffReader;
import com.op1.iff.types.ID;
import com.op1.iff.types.OSType;
import com.op1.iff.types.SignedChar;
import com.op1.iff.types.SignedLong;
import com.op1.util.Check;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApplicationChunk implements Chunk {

    private final ID chunkId = ChunkType.APPLICATION.getId();   // 4 bytes
    private SignedLong chunkSize;                               // 4 bytes
    private OSType applicationSignature;                        // 4 bytes
    private SignedChar[] data;                                  // 2 bytes per item

    private ApplicationChunk() {
    }

    private ApplicationChunk(ApplicationChunk chunk) {
        this.chunkSize = chunk.getChunkSize();
        this.applicationSignature = chunk.getApplicationSignature();
        this.data = Arrays.copyOf(chunk.getData(), chunk.getData().length);
    }

    @Override
    public ID getChunkID() {
        return chunkId;
    }

    public SignedLong getChunkSize() {
        return chunkSize;
    }

    public OSType getApplicationSignature() {
        return applicationSignature;
    }

    public SignedChar[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    public static class Builder {

        private final ApplicationChunk instance;

        public Builder() {
            instance = new ApplicationChunk();
        }

        public ApplicationChunk build() {
            Check.notNull(instance.chunkSize, "Missing chunkSize");
            Check.notNull(instance.applicationSignature, "Missing applicationSignature");
            Check.notNull(instance.getData(), "Missing data");
            return new ApplicationChunk(instance);
        }

        public Builder withChunkSize(SignedLong chunkSize) {
            instance.chunkSize = chunkSize;
            return this;
        }

        public Builder withApplicationSignature(OSType applicationSignature) {
            instance.applicationSignature = applicationSignature;
            return this;
        }

        public Builder withData(SignedChar[] data) {
            instance.data = data;
            return this;
        }
    }

    public static ApplicationChunk readApplicationChunk(IffReader reader) throws IOException {

        SignedLong chunkSize = reader.readSignedLong();
        OSType applicationSignature = reader.readOSType();

        int numCharsToRead = (chunkSize.toInt() - 4);
        List<SignedChar> data = new ArrayList<SignedChar>();
        for (int i = 0; i < numCharsToRead; i++) {
            data.add(reader.readSignedChar());
        }
        final SignedChar[] dataArray = data.toArray(new SignedChar[data.size()]);

        return new ApplicationChunk.Builder()
                .withChunkSize(chunkSize)
                .withApplicationSignature(applicationSignature)
                .withData(dataArray)
                .build();
    }

    public static class ApplicationChunkReader implements ChunkReader {
        @Override
        public Chunk readChunk(IffReader reader) throws IOException {
            return readApplicationChunk(reader);
        }
    }
}
