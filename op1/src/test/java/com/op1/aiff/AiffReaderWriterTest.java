package com.op1.aiff;

import com.op1.iff.IffReader;
import com.op1.iff.IffWriter;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.net.URL;

import static org.junit.Assert.assertTrue;

public class AiffReaderWriterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String ALBUM_FILE = "Scrap30.aif";

    @Test
    public void read_and_write_album_file_produces_same_file() throws Exception {
        doReadWriteTest(getFileFromClasspathResource(ALBUM_FILE));
    }

    @Test
    public void newTest() throws Exception {

        final File file = getFileFromClasspathResource(ALBUM_FILE);
        assertTrue(file.exists());
    }

    private void doReadWriteTest(File readFile) throws IOException {

        // given
        final File writeFile = temporaryFolder.newFile("writeFile.aif");
        final AiffReader aiffReader = newAiffReader(readFile);
        final AiffWriter aiffWriter = newAiffWriter(writeFile);

        // when
        final Aiff aiff = aiffReader.readAiff();
        aiffWriter.writeAiff(aiff);

        // then
        assertTrue(writeFile.exists());
        assertTrue(FileUtils.contentEquals(readFile, writeFile));
    }

    private AiffWriter newAiffWriter(File writeFile) throws FileNotFoundException {
        final FileOutputStream fileOutputStream = new FileOutputStream(writeFile);
        final DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
        final IffWriter iffWriter = new IffWriter(dataOutputStream);
        return new AiffWriter(iffWriter);
    }

    private AiffReader newAiffReader(File file) throws FileNotFoundException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        final DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        final IffReader iffReader = new IffReader(dataInputStream);
        return new AiffReader(iffReader);
    }

    private File getFileFromClasspathResource(String classpathResource) {

        final URL resource = AiffReaderWriterTest.class.getClassLoader().getResource(classpathResource);
        if (resource == null) {
            throw new IllegalArgumentException("Cannot find file: " + classpathResource);
        }

        return new File(resource.getFile());
    }
}
