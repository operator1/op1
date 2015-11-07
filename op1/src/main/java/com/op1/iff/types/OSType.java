package com.op1.iff.types;

import java.util.Arrays;

/**
 * 32 bits. A concatenation of four characters, as defined in Inside Macintosh, vol II.
 */
public class OSType extends DataType {

    public OSType(byte[] bytes) {
        super(bytes, 4);
    }

    public static OSType fromString(String s) {
        return new OSType(s.getBytes());
    }

    @Override
    public String toString() {
        return new String(bytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final OSType osType = (OSType) o;

        return Arrays.equals(bytes, osType.bytes);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
