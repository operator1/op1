package com.op1.aiff;

import com.op1.iff.types.ID;
import com.op1.iff.types.SignedLong;
import org.junit.Test;

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
                .withChunk(ChunkType.COMMON.getChunkId(), commonChunk)
                .withChunk(ChunkType.SOUND_DATA.getChunkId(), soundDataChunk)
                .withChunk(ChunkType.APPLICATION.getChunkId(), applicationChunk1)
                .withChunk(ChunkType.APPLICATION.getChunkId(), applicationChunk2)
                .build();

        // then
        assertThat(aiff.getChunks(ChunkType.APPLICATION.getChunkId()), notNullValue());
        assertThat(aiff.getChunks(ChunkType.APPLICATION.getChunkId()).size(), equalTo(2));
    }
}
