package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.types.ID;
import com.op1.util.ChannelSplitter;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AiffReaderWriterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final Logger LOGGER = LoggerFactory.getLogger(AiffReaderWriterTest.class);

    @Test
    public void read_and_write_album_file_produces_same_file() throws Exception {
        doReadWriteTest(ExampleFile.ALBUM.getFile());
    }

    @Test
    public void read_and_write_album_side_b_file_produces_same_file() throws Exception {
        doReadWriteTest(ExampleFile.ALBUM_SIDE_B.getFile());
    }

    @Test
    public void read_and_write_tape_file_produces_same_file() throws Exception {
        doReadWriteTest(ExampleFile.TAPE.getFile());
    }

    @Test
    public void read_and_write_drum_utility_file_produces_same_file() throws Exception {
        doReadWriteTest(ExampleFile.DRUM_UTILITY.getFile());
    }

    @Test
    public void read_and_write_drum_preset_file_produces_same_file() throws Exception {
        doReadWriteTest(ExampleFile.DRUM_PRESET_1.getFile());
    }

    @Test
    public void read_and_write_synth_preset_file_produces_same_file() throws Exception {
        doReadWriteTest(ExampleFile.SYNTH_PRESET.getFile());
    }

    @Test
    public void read_and_write_left_split_file_produces_same_file() throws Exception {
        final MonoFiles monoFiles = splitStereoFile(ExampleFile.ALBUM.getFile());
        doReadWriteTest(monoFiles.left);
    }

    @Test
    public void read_and_write_right_split_file_produces_same_file() throws Exception {
        final MonoFiles monoFiles = splitStereoFile(ExampleFile.ALBUM.getFile());
        doReadWriteTest(monoFiles.right);
    }

    private void doReadWriteTest(File readFile) throws IOException {

        // given
        LOGGER.info(format("Size of readFile: %s", FileUtils.sizeOf(readFile)));
        final File writeFile = temporaryFolder.newFile("writeFile.aif");
        final AiffReader aiffReader = AiffReader.newAiffReader(readFile);
        final AiffWriter aiffWriter = AiffWriter.newAiffWriter(writeFile);

        // when
        LOGGER.debug("\n\n\nREADING AIF\n\n\n");
        final Aiff readAiff = aiffReader.readAiff();
        LOGGER.debug(readAiff.toString());
        LOGGER.debug(format("aiff physical size: %s", readAiff.getPhysicalSize()));
        for (Map.Entry<ID, List<Chunk>> entry : readAiff.getChunksMap().entrySet()) {
            for (Chunk chunk : entry.getValue()) {
                LOGGER.debug(String.format("Chunk: %s", chunk.toString()));
                LOGGER.debug(format("%s chunk physical size: %s", chunk.getChunkID().getName(), chunk.getPhysicalSize()));
            }
        }

        LOGGER.debug("\n\n\nWRITING AIF\n\n\n");
        aiffWriter.writeAiff(readAiff);

        aiffReader.close();
        aiffWriter.close();

        // then
        assertTrue(writeFile.exists());
        LOGGER.info(format("Size of readFile: %s", FileUtils.sizeOf(readFile)));
        LOGGER.info(format("Size of writeFile: %s", FileUtils.sizeOf(writeFile)));
        assertTrue(FileUtils.contentEquals(readFile, writeFile));
    }

    private MonoFiles splitStereoFile(File stereoFile) throws Exception {

        final AiffReader aiffReader = AiffReader.newAiffReader(stereoFile);
        LOGGER.debug("\n\n\nREADING STEREO AIF\n\n\n");
        final Aiff aiff = aiffReader.readAiff();
        Aiff[] aiffs = new ChannelSplitter().splitChannels(aiff);

        assertThat(aiffs.length, equalTo(2));
        final File leftFile = temporaryFolder.newFile("left.aif");
        final File rightFile = temporaryFolder.newFile("right.aif");

        final AiffWriter leftWriter = AiffWriter.newAiffWriter(leftFile);
        final AiffWriter rightWriter = AiffWriter.newAiffWriter(rightFile);

        LOGGER.debug("\n\n\nWRITING LEFT AIF\n\n\n");
        leftWriter.writeAiff(aiffs[0]);
        LOGGER.debug("\n\n\nWRITING RIGHT AIF\n\n\n");
        rightWriter.writeAiff(aiffs[1]);

        return new MonoFiles(leftFile, rightFile);
    }

    private static class MonoFiles {

        private final File left;
        private final File right;

        public MonoFiles(File left, File right) {
            this.left = left;
            this.right = right;
        }
    }
}
