package com.op1.iff.types;

/**
 * 8 bits, unsigned. Contains any number from zero to 255 (inclusive).
 */
public class UnsignedChar extends DataType {

    public UnsignedChar(byte b) {
        super(new byte[] { b });
    }
}
