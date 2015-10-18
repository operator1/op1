package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.types.SignedLong;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class AiffReaderTest {

    @Test
    public void canReadDrumKitFile() throws Exception {
        doReadFileTest(ExampleFile.DRUM_UTILITY.getFile());
    }

    @Test
    public void canReadAlbumFile() throws Exception {
        doReadFileTest(ExampleFile.ALBUM.getFile());
    }

    private void doReadFileTest(File file) throws Exception {

        // given
        final AiffReader aiffReader = AiffReader.newAiffReader(file);

        // when
        final Aiff aiff = aiffReader.readAiff();

        // then
        assertThat(aiff, notNullValue());
    }

    @Test
    public void canReadDrumPreset1() throws Exception {
        doCanReadDrumPreset(ExampleFile.DRUM_PRESET_1.getFile());
    }

    private void doCanReadDrumPreset(File drumPreset) throws Exception {

        // given
        final AiffReader aiffReader = AiffReader.newAiffReader(drumPreset);

        // when
        final Aiff aiff = aiffReader.readAiff();

        // then
        assertThat(aiff, notNullValue());
        assertHasApplicationChunk(aiff);
    }

    private void assertHasApplicationChunk(Aiff aiff) throws Exception {
        assertThat(aiff, notNullValue());
        final List<Chunk> applicationChunks = aiff.getChunks(ChunkType.APPLICATION.getChunkId());
        assertThat(applicationChunks.size(), equalTo(1));
        final ApplicationChunk applicationChunk = (ApplicationChunk) applicationChunks.get(0);
        assertThat(applicationChunk.getChunkSize(), equalTo(SignedLong.fromInt(1248)));
    }
}
