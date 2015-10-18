package com.op1.aiff;

import com.op1.iff.IffReader;
import com.op1.iff.IffWriter;
import com.op1.util.ChannelSplitter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ChannelSplitterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String ALBUM_FILE = "side_b.aif";

    @Test
    public void canSplitStereoFile() throws Exception {

        final InputStream inputStream = ChannelSplitterTest.class.getClassLoader().getResourceAsStream(ALBUM_FILE);
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        final IffReader iffReader = new IffReader(dataInputStream);
        final AiffReader aiffReader = new AiffReader(iffReader);

        final Aiff aiff = aiffReader.readAiff();
        final Aiff[] aiffs = new ChannelSplitter().splitChannels(aiff);
        assertThat(aiffs.length, equalTo(2));
        writeAiff(aiffs[0], temporaryFolder.newFile("left.aif"));
        writeAiff(aiffs[1], temporaryFolder.newFile("right.aif"));
    }

    private void writeAiff(Aiff aiff, File writeFile) throws IOException {

        System.out.println(writeFile.getAbsolutePath());
        final FileOutputStream fileOutputStream = new FileOutputStream(writeFile);
        final DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
        final IffWriter iffWriter = new IffWriter(dataOutputStream);
        final AiffWriter aiffWriter = new AiffWriter(iffWriter);
        aiffWriter.writeAiff(aiff);
    }
}
