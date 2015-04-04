package com.op1.aiff;

import com.op1.iff.IffReader;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class AiffReaderTest {

    private static final String ALBUM_FILE = "Scrap30.aif";
    private static final String DRUMKIT_FILE = "PO-12.aif";

    @Test
    public void canReadDrumKitFile() throws Exception {
        doReadFileTest(DRUMKIT_FILE);
    }

    @Test
    public void canReadAlbumFile() throws Exception {
        doReadFileTest(ALBUM_FILE);
    }

    private void doReadFileTest(String file) throws Exception {

        // given
        final InputStream inputStream = AiffReaderTest.class.getClassLoader().getResourceAsStream(file);
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        final IffReader iffReader = new IffReader(dataInputStream);
        final AiffReader aiffReader = new AiffReader(iffReader);

        // when
        final Aiff aiff = aiffReader.readAiff();

        // then
        assertThat(aiff, notNullValue());
    }
}
