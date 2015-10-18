package com.op1.iff.types;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 32 bits, signed. Contains any number from -2,147,483,648 to 2,147,483,647 (inclusive).
 */
public class SignedLong extends DataType {


    public SignedLong(byte[] bytes) {
        super(bytes, 4);
    }

    public int toInt() {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static SignedLong fromInt(int i) {
        return new SignedLong(ByteBuffer.allocate(4).putInt(i).array());
    }

    @Override
    public String toString() {
        return String.valueOf(toInt());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SignedLong that = (SignedLong) o;

        return Arrays.equals(bytes, that.bytes);

    }

    @Override
    public int hashCode() {
        //noinspection ConstantConditions
        return bytes != null ? Arrays.hashCode(bytes) : 0;
    }
}
