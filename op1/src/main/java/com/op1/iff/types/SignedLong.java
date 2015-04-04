package com.op1.iff.types;

import java.nio.ByteBuffer;

/**
 * 32 bits, signed. Contains any number from -2,147,483,648 to 2,147,483,647 (inclusive).
 */
public class SignedLong extends DataType {

    public SignedLong(byte[] bytes) {
        super(bytes);
    }

    public int toInt() {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static SignedLong fromInt(int i) {
        return new SignedLong(ByteBuffer.allocate(4).putInt(i).array());
    }
}
