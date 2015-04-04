package com.op1.iff.types;

import java.nio.ByteBuffer;

/**
 * 32 bits, unsigned. Contains any number from zero to 4,294,967,295 (inclusive).
 */
public class UnsignedLong extends DataType {

    public UnsignedLong(byte[] bytes) {
        super(bytes);
    }

    public long toLong() {
        byte[] bufferBytes = new byte[8];
        bufferBytes[0] = 0;
        bufferBytes[1] = 0;
        bufferBytes[2] = 0;
        bufferBytes[3] = 0;
        System.arraycopy(bytes, 0, bufferBytes, 4, bytes.length);
        return ByteBuffer.wrap(bufferBytes).getLong();
    }
}
