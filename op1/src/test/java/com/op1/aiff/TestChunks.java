package com.op1.aiff;

import com.op1.iff.types.*;

import java.nio.ByteBuffer;

public class TestChunks {

    public static CommonChunk commonChunkOneSampleFrame16BitMono() {
        return new CommonChunk.Builder()
                .withChunkSize(SignedLong.fromInt(18))
                .withNumChannels(SignedShort.fromShort((short) 1))
                .withNumSampleFrames(UnsignedLong.fromLong(1))
                .withSampleSize(SignedShort.fromShort((short) 16))
                .withSampleRate(new Extended(ByteBuffer.allocate(10).array()))
                .build();
    }

    public static SoundDataChunk soundDataChunkOneSampleFrame16BitMono() {
        return new SoundDataChunk.Builder()
                .withChunkSize(SignedLong.fromInt(10))
                .withOffset(UnsignedLong.fromLong(0))
                .withBlockSize(UnsignedLong.fromLong(0))
                .withSampleData(ByteBuffer.allocate(2).array())
                .build();
    }

    public static ApplicationChunk applicationChunk() {
        return new ApplicationChunk.Builder()
                .withChunkSize(SignedLong.fromInt(8))
                .withApplicationSignature(OSType.fromString("TEST"))
                .withData(new SignedChar[] {new SignedChar((byte) 0), new SignedChar((byte) 0)})
                .build();
    }
}
