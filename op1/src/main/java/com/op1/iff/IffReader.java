package com.op1.iff;

import com.op1.iff.types.*;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

public class IffReader implements Closeable {

    private final DataInputStream dataInputStream;

    public IffReader(DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }

    public SignedChar readSignedChar() throws IOException {
        return new SignedChar(dataInputStream.readByte());
    }

    public UnsignedChar readUnsignedChar() throws IOException {
        return new UnsignedChar(dataInputStream.readByte());
    }

    public SignedShort readSignedShort() throws IOException {
        return new SignedShort(readBytes(2));
    }

    public UnsignedShort readUnsignedShort() throws IOException {
        return new UnsignedShort(readBytes(2));
    }

    public SignedLong readSignedLong() throws IOException {
        return new SignedLong(readBytes(4));
    }

    public UnsignedLong readUnsignedLong() throws IOException {
        return new UnsignedLong(readBytes(4));
    }

    public Extended readExtended() throws IOException {
        return new Extended(readBytes(10));
    }

    public PString readPString() throws IOException {

        // The first byte contains a count of the text textBytes that follow.
        final int numTextBytes = dataInputStream.readUnsignedByte();

        // Read the text bytes.
        final byte[] textBytes = readBytes(numTextBytes);

        // The total number of textBytes should be even. A pad byte may have been added to satisfy this rule.
        int numPadBytes = (numTextBytes + 1) % 2;

        byte[] pStringBytes = new byte[1 + numTextBytes + numPadBytes];
        pStringBytes[0] = (byte) numTextBytes;
        System.arraycopy(textBytes, 0, pStringBytes, 1, textBytes.length);
        if (numPadBytes == 1) {
            pStringBytes[pStringBytes.length - 1] = dataInputStream.readByte();
        }
        return new PString(pStringBytes);
    }

    public ID readID() throws IOException {
        return new ID(readBytes(4));
    }

    public OSType readOSType() throws IOException {
        return new OSType(readBytes(4));
    }

    public byte[] readBytes(int numBytesToRead) throws IOException {
        final byte[] bytes = new byte[numBytesToRead];
        final int numRead = dataInputStream.read(bytes);
        if (numRead != numBytesToRead) {
            throw new EOFException(String.format("numBytesToRead: %s, numRead: %s", numBytesToRead, numRead));
        }
        return bytes;
    }

    public byte readByte() throws IOException {
        return dataInputStream.readByte();
    }

    public void close() throws IOException {
        dataInputStream.close();
    }
}
