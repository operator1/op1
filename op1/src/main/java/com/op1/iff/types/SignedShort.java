package com.op1.iff.types;

import java.nio.ByteBuffer;
import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SignedShort that = (SignedShort) o;

        return Arrays.equals(bytes, that.bytes);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
