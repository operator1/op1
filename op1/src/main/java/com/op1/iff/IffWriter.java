package com.op1.iff;

import com.op1.iff.types.*;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;

public class IffWriter implements Closeable {

    private final DataOutputStream dataOutputStream;

    public IffWriter(DataOutputStream dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
    }

    public void write(SignedChar data) throws IOException {
        this.dataOutputStream.write(data.toByteArray());
    }

    public void write(UnsignedChar data) throws IOException {
        this.dataOutputStream.write(data.toByteArray());
    }

    public void write(SignedShort data) throws IOException {
        this.dataOutputStream.write(data.toByteArray());
    }

    public void write(UnsignedShort data) throws IOException {
        this.dataOutputStream.write(data.toByteArray());
    }

    public void write(SignedLong data) throws IOException {
        this.dataOutputStream.write(data.toByteArray());
    }

    public void write(UnsignedLong data) throws IOException {
        this.dataOutputStream.write(data.toByteArray());
    }

    public void write(Extended data) throws IOException {
        this.dataOutputStream.write(data.toByteArray());
    }

    public void write(PString data) throws IOException {
        this.dataOutputStream.write(data.toByteArray());
    }

    public void write(ID data) throws IOException {
        this.dataOutputStream.write(data.toByteArray());
    }

    public void write(OSType data) throws IOException {
        this.dataOutputStream.write(data.toByteArray());
    }

    public void write(byte[] bytes) throws IOException {
        this.dataOutputStream.write(bytes);
    }

    public void flush() throws IOException {
        this.dataOutputStream.flush();
    }

    public void close() throws IOException {
        this.dataOutputStream.close();
    }
}
