package com.op1.aiff;

import com.op1.iff.types.ID;
import com.op1.iff.types.SignedLong;
import org.junit.Test;

import static com.op1.aiff.ChunkType.COMMON;
import static com.op1.aiff.ChunkType.SOUND_DATA;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AiffTest {

    @Test
    public void canAddMultipleApplicationChunks() throws Exception {

        // given
        CommonChunk commonChunk = TestChunks.commonChunkOneSampleFrame16BitMono();
        SoundDataChunk soundDataChunk = TestChunks.soundDataChunkOneSampleFrame16BitMono();
        ApplicationChunk applicationChunk1 = TestChunks.applicationChunk();
        ApplicationChunk applicationChunk2 = TestChunks.applicationChunk();

        // when
        Aiff aiff = new Aiff.Builder()
                .withChunkId(new ID("FORM".getBytes()))
                .withChunkSize(SignedLong.fromInt(42))
                .withFormType(new ID("AIFF".getBytes()))
                .withChunk(COMMON.getChunkId(), commonChunk)
                .withChunk(SOUND_DATA.getChunkId(), soundDataChunk)
                .withChunk(ChunkType.APPLICATION.getChunkId(), applicationChunk1)
                .withChunk(ChunkType.APPLICATION.getChunkId(), applicationChunk2)
                .build();

        // then
        assertThat(aiff.getChunks(ChunkType.APPLICATION.getChunkId()), notNullValue());
        assertThat(aiff.getChunks(ChunkType.APPLICATION.getChunkId()).size(), equalTo(2));
    }

    @Test(expected = IllegalStateException.class)
    public void cannotAddMultipleCommonChunks() throws Exception {

        // given
        CommonChunk commonChunk1 = TestChunks.commonChunkOneSampleFrame16BitMono();
        CommonChunk commonChunk2 = TestChunks.commonChunkOneSampleFrame16BitMono();
        SoundDataChunk soundDataChunk = TestChunks.soundDataChunkOneSampleFrame16BitMono();

        // when
        new Aiff.Builder()
                .withChunkId(new ID("FORM".getBytes()))
                .withChunkSize(SignedLong.fromInt(42))
                .withFormType(new ID("AIFF".getBytes()))
                .withChunk(COMMON.getChunkId(), commonChunk1)
                .withChunk(COMMON.getChunkId(), commonChunk2)
                .withChunk(SOUND_DATA.getChunkId(), soundDataChunk)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void cannotAddMultipleSoundDataChunks() throws Exception {

        // given
        CommonChunk commonChunk = TestChunks.commonChunkOneSampleFrame16BitMono();
        SoundDataChunk soundDataChunk1 = TestChunks.soundDataChunkOneSampleFrame16BitMono();
        SoundDataChunk soundDataChunk2 = TestChunks.soundDataChunkOneSampleFrame16BitMono();

        // when
        new Aiff.Builder()
                .withChunkId(new ID("FORM".getBytes()))
                .withChunkSize(SignedLong.fromInt(42))
                .withFormType(new ID("AIFF".getBytes()))
                .withChunk(COMMON.getChunkId(), commonChunk)
                .withChunk(SOUND_DATA.getChunkId(), soundDataChunk1)
                .withChunk(SOUND_DATA.getChunkId(), soundDataChunk2)
                .build();
    }

    @Test
    public void canHaveZeroSoundDataChunksWhenNumSampleFramesIsZero() throws Exception {

        // given
        CommonChunk commonChunk = TestChunks.commonChunkZeroSampleFrames8BitMono();

        // when
        Aiff aiff = new Aiff.Builder()
                .withChunkId(new ID("FORM".getBytes()))
                .withChunkSize(SignedLong.fromInt(22))
                .withFormType(new ID("AIFF".getBytes()))
                .withChunk(COMMON.getChunkId(), commonChunk)
                .build();

        // then
        assertThat(aiff, notNullValue());
    }

    @Test
    public void canHaveSoundDataChunksEvenWhenNumSampleFramesIsZero() throws Exception {

        // given
        CommonChunk commonChunk = TestChunks.commonChunkZeroSampleFrames8BitMono();
        SoundDataChunk soundDataChunk = TestChunks.soundDataChunkOneSampleFrame16BitMono();

        // when
        Aiff aiff = new Aiff.Builder()
                .withChunkId(new ID("FORM".getBytes()))
                .withChunkSize(SignedLong.fromInt(32))
                .withFormType(new ID("AIFF".getBytes()))
                .withChunk(COMMON.getChunkId(), commonChunk)
                .withChunk(SOUND_DATA.getChunkId(), soundDataChunk)
                .build();

        // then
        assertThat(aiff, notNullValue());
    }

    @Test(expected = IllegalStateException.class)
    public void cannotOmitCommonChunk() throws Exception {
        new Aiff.Builder()
                .withChunkId(new ID("FORM".getBytes()))
                .withChunkSize(SignedLong.fromInt(4))
                .withFormType(new ID("AIFF".getBytes()))
                .build();
    }
}
