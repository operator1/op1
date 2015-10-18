package com.op1.util;

import com.op1.aiff.FormatVersionChunk;
import com.op1.iff.types.*;

public class Op1Constants {

    public static final Extended SAMPLE_RATE_44100 = new Extended(new byte[]{
            64, 14, -84, 68, 0, 0, 0, 0, 0, 0
    });

    public static final byte[] COMMON_CHUNK_DESCRIPTION = ")Signed integer (little-endian) linear PCM".getBytes();
    public static final OSType APPLICATION_CHUNK_SIGNATURE = OSType.fromString("op-1");

    public static final SignedShort NUM_CHANNELS_MONO = SignedShort.fromShort((short) 1);
    public static final SignedShort SAMPLE_SIZE_16_BIT = SignedShort.fromShort((short) 16);
    public static final ID ID_SOWT = ID.valueOf("sowt");

    public static final FormatVersionChunk FORMAT_VERSION_CHUNK = new FormatVersionChunk.Builder()
            .withChunkSize(SignedLong.fromInt(4))
            .withTimestamp(new UnsignedLong(new byte[]{-94, -128, 81, 64}))
            .build();
}
