package com.op1.aiff;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class AiffReaderWriterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void read_and_write_album_file_produces_same_file() throws Exception {
        doReadWriteTest(ExampleFile.ALBUM.getFile());
    }

    @Test
    public void read_and_write_tape_file_produces_same_file() throws Exception {
        doReadWriteTest(ExampleFile.TAPE.getFile());
    }

    @Test
    public void read_and_write_drum_file_produces_same_file() throws Exception {
        doReadWriteTest(ExampleFile.DRUM.getFile());
    }

    private void doReadWriteTest(File readFile) throws IOException {

        // given
        final File writeFile = temporaryFolder.newFile("writeFile.aif");
        final AiffReader aiffReader = AiffReader.newAiffReader(readFile);
        final AiffWriter aiffWriter = AiffWriter.newAiffWriter(writeFile);

        // when
        aiffWriter.writeAiff(aiffReader.readAiff());

        // then
        assertTrue(writeFile.exists());
        assertTrue(FileUtils.contentEquals(readFile, writeFile));
    }
}
