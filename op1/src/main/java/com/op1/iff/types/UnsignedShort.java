package com.op1.iff.types;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 16 bits, unsigned. Contains any number from zero to 65,535 (inclusive).
 */
public class UnsignedShort extends DataType {

    public UnsignedShort(byte[] bytes) {
        super(bytes, 2);
    }

    public int toInt() {
        byte[] bufferBytes = new byte[4];
        bufferBytes[0] = 0;
        bufferBytes[1] = 0;
        System.arraycopy(bytes, 0, bufferBytes, 2, bytes.length);
        return ByteBuffer.wrap(bufferBytes).getInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UnsignedShort that = (UnsignedShort) o;

        return Arrays.equals(bytes, that.bytes);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        return String.valueOf(toInt());
    }
}
