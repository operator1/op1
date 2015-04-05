package com.op1.iff.types;

import java.nio.ByteBuffer;

/**
 * 32 bits, unsigned. Contains any number from zero to 4,294,967,295 (inclusive).
 */
public class UnsignedLong extends DataType {

    public UnsignedLong(byte[] bytes) {
        super(bytes);
    }

    public static UnsignedLong fromLong(long someLong) {
        return new UnsignedLong(ByteBuffer.allocate(8).putLong(someLong).array());
    }
}
