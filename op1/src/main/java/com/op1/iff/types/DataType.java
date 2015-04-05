package com.op1.iff.types;

import com.op1.util.Check;

import java.util.Arrays;

public abstract class DataType {

    protected final byte[] bytes;

    public DataType(byte[] bytes) {
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public DataType(byte[] bytes, int expectedNumBytes) {
        Check.that(bytes.length == expectedNumBytes, "Unexpected number of bytes: " + bytes.length);
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
