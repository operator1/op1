package com.op1.iff.types;

import java.util.Arrays;

/**
 * 8 bits, unsigned. Contains any number from zero to 255 (inclusive).
 */
public class UnsignedChar extends DataType {

    public UnsignedChar(byte b) {
        super(new byte[] { b });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UnsignedChar that = (UnsignedChar) o;

        return Arrays.equals(bytes, that.bytes);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
