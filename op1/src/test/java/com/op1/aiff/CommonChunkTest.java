package com.op1.aiff;

import com.op1.iff.types.ID;
import com.op1.iff.types.SignedLong;
import com.op1.iff.types.SignedShort;
import com.op1.iff.types.UnsignedLong;
import com.op1.util.Op1Constants;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class CommonChunkTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void cannotBuildCommonChunkWithoutChunkSize() throws Exception {

        // given
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Missing chunkSize");
        final CommonChunk.Builder builder = new CommonChunk.Builder()
                .withNumChannels(SignedShort.fromShort((short) 1))
                .withNumSampleFrames(UnsignedLong.fromLong(1024))
                .withSampleSize(SignedShort.fromShort((short) 16))
                .withSampleRate(Op1Constants.SAMPLE_RATE_44100)
                .withCodec(ID.valueOf("BLAH"))
                .withDescription("1234567890".getBytes());

        // when
        builder.build();

        // then boom!
    }

    @Test
    public void cannotBuildCommonChunkWithoutNumChannels() throws Exception {

        // given
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Missing numChannels");
        final CommonChunk.Builder builder = new CommonChunk.Builder()
                .withChunkSize(SignedLong.fromInt(30))
                .withNumSampleFrames(UnsignedLong.fromLong(1024))
                .withSampleSize(SignedShort.fromShort((short) 16))
                .withSampleRate(Op1Constants.SAMPLE_RATE_44100)
                .withCodec(ID.valueOf("BLAH"))
                .withDescription("12345678".getBytes());

        // when
        builder.build();

        // then boom!
    }

    @Test
    public void cannotBuildCommonChunkWithoutNumSampleFrames() throws Exception {

        // given
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Missing numSampleFrames");
        final CommonChunk.Builder builder = new CommonChunk.Builder()
                .withChunkSize(SignedLong.fromInt(30))
                .withNumChannels(SignedShort.fromShort((short) 1))
                .withSampleSize(SignedShort.fromShort((short) 16))
                .withSampleRate(Op1Constants.SAMPLE_RATE_44100)
                .withCodec(ID.valueOf("BLAH"))
                .withDescription("12345678".getBytes());

        // when
        builder.build();

        // then boom!
    }

    @Test
    public void cannotBuildCommonChunkWithoutSampleSize() throws Exception {

        // given
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Missing sampleSize");
        final CommonChunk.Builder builder = new CommonChunk.Builder()
                .withChunkSize(SignedLong.fromInt(30))
                .withNumChannels(SignedShort.fromShort((short) 1))
                .withNumSampleFrames(UnsignedLong.fromLong(1024))
                .withSampleRate(Op1Constants.SAMPLE_RATE_44100)
                .withCodec(ID.valueOf("BLAH"))
                .withDescription("12345678".getBytes());

        // when
        builder.build();

        // then boom!
    }

    @Test
    public void cannotBuildCommonChunkWithoutSampleRate() throws Exception {

        // given
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Missing sampleRate");
        final CommonChunk.Builder builder = new CommonChunk.Builder()
                .withChunkSize(SignedLong.fromInt(30))
                .withNumChannels(SignedShort.fromShort((short) 1))
                .withNumSampleFrames(UnsignedLong.fromLong(1024))
                .withSampleSize(SignedShort.fromShort((short) 16))
                .withCodec(ID.valueOf("BLAH"))
                .withDescription("12345678".getBytes());

        // when
        builder.build();

        // then boom!
    }

    @Test
    public void canBuildCommonChunkWithoutCodecAndDescription() throws Exception {

        // given
        final CommonChunk.Builder builder = new CommonChunk.Builder()
                .withChunkSize(SignedLong.fromInt(18))
                .withNumChannels(SignedShort.fromShort((short) 1))
                .withNumSampleFrames(UnsignedLong.fromLong(1024))
                .withSampleSize(SignedShort.fromShort((short) 16))
                .withSampleRate(Op1Constants.SAMPLE_RATE_44100);

        // when
        builder.build();

        // then boom!
    }

    @Test
    public void canBuildCommonChunkWithCodecAndDescription() throws Exception {

        // given
        final CommonChunk.Builder builder = new CommonChunk.Builder()
                .withChunkSize(SignedLong.fromInt(30))
                .withNumChannels(SignedShort.fromShort((short) 1))
                .withNumSampleFrames(UnsignedLong.fromLong(1024))
                .withSampleSize(SignedShort.fromShort((short) 16))
                .withSampleRate(Op1Constants.SAMPLE_RATE_44100)
                .withCodec(ID.valueOf("BLAH"))
                .withDescription("12345678".getBytes());

        // when
        builder.build();

        // then boom!
    }

    @Test
    public void extraPadByteIsAddedWhenDescriptionHasOddLength() throws Exception {

        // given
        final CommonChunk.Builder builder = new CommonChunk.Builder()
                .withChunkSize(SignedLong.fromInt(29))
                .withNumChannels(SignedShort.fromShort((short) 1))
                .withNumSampleFrames(UnsignedLong.fromLong(1024))
                .withSampleSize(SignedShort.fromShort((short) 16))
                .withSampleRate(Op1Constants.SAMPLE_RATE_44100)
                .withCodec(ID.valueOf("BLAH"))
                .withDescription("1234567".getBytes());

        // when
        final CommonChunk commonChunk = builder.build();

        // then
        assertThat(commonChunk.getDescription().length, equalTo(8));
    }

    @Test
    public void noExtraPadByteIsAddedWhenDescriptionHasEvenLength() throws Exception {

        // given
        final CommonChunk.Builder builder = new CommonChunk.Builder()
                .withChunkSize(SignedLong.fromInt(30))
                .withNumChannels(SignedShort.fromShort((short) 1))
                .withNumSampleFrames(UnsignedLong.fromLong(1024))
                .withSampleSize(SignedShort.fromShort((short) 16))
                .withSampleRate(Op1Constants.SAMPLE_RATE_44100)
                .withCodec(ID.valueOf("BLAH"))
                .withDescription("12345678".getBytes());

        // when
        final CommonChunk commonChunk = builder.build();

        // then
        assertThat(commonChunk.getDescription().length, equalTo(8));
    }
}