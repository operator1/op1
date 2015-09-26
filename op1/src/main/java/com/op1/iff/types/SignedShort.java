package com.op1.iff.types;

import java.nio.ByteBuffer;

/**
 * 16 bits, signed. Contains any number from -32,768 to 32,767 (inclusive).
 */
public class SignedShort extends DataType {

    public SignedShort(byte[] bytes) {
        super(bytes, 2);
    }

    public short toShort() {
        return ByteBuffer.wrap(bytes).getShort();
    }

    public static SignedShort fromShort(short s) {
        return new SignedShort(ByteBuffer.allocate(2).putShort(s).array());
    }

    @Override
    public String toString() {
        return String.valueOf(toShort());
    }
}
