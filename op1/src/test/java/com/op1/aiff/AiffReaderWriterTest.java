package com.op1.aiff;

import com.op1.iff.IffReader;
import com.op1.iff.IffWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;

import static org.junit.Assert.*;

public class AiffReaderWriterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String ALBUM_FILE = "Scrap30.aif";

    @Test
    public void read_and_write_album_file_produces_same_file() throws Exception {
        doReadWriteTest(ALBUM_FILE);
    }

    private void doReadWriteTest(String file) throws IOException {

        // given
        final InputStream inputStream = AiffReaderWriterTest.class.getClassLoader().getResourceAsStream(file);
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        final IffReader iffReader = new IffReader(dataInputStream);
        final AiffReader aiffReader = new AiffReader(iffReader);
        final File writeFile = temporaryFolder.newFile("writeFile.aif");
        System.out.println(writeFile.getAbsolutePath());
        final FileOutputStream fileOutputStream = new FileOutputStream(writeFile);
        final DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
        final IffWriter iffWriter = new IffWriter(dataOutputStream);
        final AiffWriter aiffWriter = new AiffWriter(iffWriter);

        // when
        final Aiff aiff = aiffReader.readAiff();
        aiffWriter.writeAiff(aiff);

        // then
        assertTrue(writeFile.exists());
    }
}
