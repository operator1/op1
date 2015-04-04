package com.op1.iff.types;

import java.util.Arrays;

public abstract class DataType {

    protected final byte[] bytes;

    public DataType(byte[] bytes) {
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
