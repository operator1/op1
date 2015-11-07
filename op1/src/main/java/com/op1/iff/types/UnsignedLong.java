package com.op1.iff.types;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 32 bits, unsigned. Contains any number from zero to 4,294,967,295 (inclusive).
 */
public class UnsignedLong extends DataType {

    public UnsignedLong(byte[] bytes) {
        super(bytes, 4);
    }

    public long toLong() {

        // TODO: Horrible implementation... improve!
        byte[] bufferBytes = new byte[8];
        bufferBytes[0] = 0;
        bufferBytes[1] = 0;
        bufferBytes[2] = 0;
        bufferBytes[3] = 0;
        System.arraycopy(bytes, 0, bufferBytes, 4, bytes.length);
        return ByteBuffer.wrap(bufferBytes).getLong();
    }

    public static UnsignedLong fromLong(long someLong) {
        return new UnsignedLong(ByteBuffer.allocate(4).putInt((int) someLong).array());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UnsignedLong that = (UnsignedLong) o;

        return Arrays.equals(bytes, that.bytes);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        return String.valueOf(toLong());
    }
}
