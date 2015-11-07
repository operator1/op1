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

    private final ID chunkId = ChunkType.APPLICATION.getChunkId();  // 4 bytes
    private SignedLong chunkSize;                                   // 4 bytes
    private OSType applicationSignature;                            // 4 bytes
    private SignedChar[] data;                                      // 1 byte per item

    // TODO: this is totally wrong. This needs to be done in a generic way across all chunks.
    private byte[] padBytes;                                        // 0 or 1 bytes

    private ApplicationChunk() {
    }

    private ApplicationChunk(ApplicationChunk chunk) {

        this.chunkSize = chunk.getChunkSize();
        this.applicationSignature = chunk.getApplicationSignature();
        this.data = Arrays.copyOf(chunk.getData(), chunk.getData().length);

        int physicalSize = chunkId.getSize()
                + chunkSize.getSize()
                + applicationSignature.getSize();

        for (SignedChar signedChar : data) {
            physicalSize += signedChar.getSize();
        }

        if (physicalSize % 2 == 1) {
            padBytes = new byte[]{0};
        } else {
            padBytes = new byte[0];
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ApplicationChunk that = (ApplicationChunk) o;

        if (!chunkId.equals(that.chunkId)) return false;
        if (!chunkSize.equals(that.chunkSize)) return false;
        if (!applicationSignature.equals(that.applicationSignature)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(data, that.data)) return false;
        return Arrays.equals(padBytes, that.padBytes);

    }

    @Override
    public int hashCode() {
        int result = chunkId.hashCode();
        result = 31 * result + chunkSize.hashCode();
        result = 31 * result + applicationSignature.hashCode();
        result = 31 * result + Arrays.hashCode(data);
        result = 31 * result + Arrays.hashCode(padBytes);
        return result;
    }

    @Override
    public String toString() {
        return "ApplicationChunk{" +
                "chunkId=" + chunkId +
                ", chunkSize=" + chunkSize +
                ", applicationSignature=" + applicationSignature +
                ", data is " + data.length + " bytes" +
                '}';
    }

    @Override
    public int getPhysicalSize() {

        int size = chunkId.getSize()
                + chunkSize.getSize()
                + applicationSignature.getSize();

        for (SignedChar signedChar : data) {
            size += signedChar.getSize();
        }

        size += padBytes.length;

        return size;
    }

    @Override
    public ID getChunkID() {
        return chunkId;
    }

    @Override
    public SignedLong getChunkSize() {
        return chunkSize;
    }

    public OSType getApplicationSignature() {
        return applicationSignature;
    }

    public SignedChar[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    public String getDataAsString() {
        return SignedChar.getString(getData());
    }

    public byte[] getPadBytes() {
        return padBytes;
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
        List<SignedChar> data = getApplicationData(reader, numCharsToRead);
        final SignedChar[] dataArray = data.toArray(new SignedChar[data.size()]);

        // Chunks must be an even number of bytes in length. If the application data portion has an odd number
        // of bytes, read and discard a pad byte.
        if (numCharsToRead % 2 == 1) {
            reader.readByte();
        }

        return new ApplicationChunk.Builder()
                .withChunkSize(chunkSize)
                .withApplicationSignature(applicationSignature)
                .withData(dataArray)
                .build();
    }

    private static List<SignedChar> getApplicationData(IffReader reader, int numCharsToRead) throws IOException {
        final List<SignedChar> data1 = new ArrayList<>(numCharsToRead);
        for (int i = 0; i < numCharsToRead; i++) {
            data1.add(new SignedChar(reader.readByte()));
        }
        return data1;
    }

    public static class ApplicationChunkReader implements ChunkReader {
        public Chunk readChunk(IffReader reader) throws IOException {
            return readApplicationChunk(reader);
        }
    }
}
