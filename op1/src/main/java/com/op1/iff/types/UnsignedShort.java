package com.op1.iff.types;

import java.nio.ByteBuffer;

/**
 * 16 bits, unsigned. Contains any number from zero to 65,535 (inclusive).
 */
public class UnsignedShort extends DataType {

    public UnsignedShort(byte[] bytes) {
        super(bytes);
    }

    public int toInt() {
        byte[] bufferBytes = new byte[4];
        bufferBytes[0] = 0;
        bufferBytes[1] = 0;
        System.arraycopy(bytes, 0, bufferBytes, 2, bytes.length);
        return ByteBuffer.wrap(bufferBytes).getInt();
    }
}
