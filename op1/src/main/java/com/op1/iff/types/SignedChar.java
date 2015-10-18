package com.op1.iff.types;

/**
 * 8 bits, signed.
 * A char can contain more than just ASCII characters. It can contain any number from -128 to 127 (inclusive).
 */
public class SignedChar extends DataType {

    public SignedChar(byte b) {
        super(new byte[] { b });
    }

    public char toChar() {
        return (char) toByteArray()[0];
    }

    public static String getString(SignedChar[] signedChars) {
        char[] chars = new char[signedChars.length];
        for (int i = 0; i < signedChars.length; i++) {
            SignedChar signedChar = signedChars[i];
            chars[i] = signedChar.toChar();
        }
        return String.valueOf(chars);
    }

    public static SignedChar[] fromString(String s) {

        final byte[] bytes = s.getBytes();
        final SignedChar[] signedChars = new SignedChar[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            signedChars[i] = new SignedChar(bytes[i]);
        }
        return signedChars;
    }

    public String toString() {
        return String.valueOf((short) toByteArray()[0]);
    }

}
