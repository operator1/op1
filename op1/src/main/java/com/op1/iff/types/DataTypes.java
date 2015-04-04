package com.op1.iff.types;

import java.util.Arrays;

public class DataTypes {

    public static Extended copyOf(Extended original) {
        return new Extended(original.toByteArray());
    }

    public static ID copyOf(ID original) {
        return new ID(original.toByteArray());
    }

    public static OSType copyOf(OSType original) {
        return new OSType(original.toByteArray());
    }

    public static PString copyOf(PString original) {
        return new PString(original.toByteArray());
    }

    public static SignedChar copyOf(SignedChar original) {
        return new SignedChar(original.toByteArray()[0]);
    }

    public static SignedLong copyOf(SignedLong signedLong) {
        return new SignedLong(signedLong.toByteArray());
    }

    public static SignedShort copyOf(SignedShort original) {
        return new SignedShort(original.toByteArray());
    }

    public static UnsignedChar copyOf(UnsignedChar original) {
        return new UnsignedChar(original.toByteArray()[0]);
    }

    public static UnsignedLong copyOf(UnsignedLong original) {
        return new UnsignedLong(original.toByteArray());
    }

    public static UnsignedShort copyOf(UnsignedShort original) {
        return new UnsignedShort(original.toByteArray());
    }

    public static byte[] copyOf(byte[] original) {
        return Arrays.copyOf(original, original.length);
    }
}
