package com.op1.aiff;

import com.op1.drumkit.DrumkitMeta;
import com.op1.util.ComplianceCheck;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ReadFileTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadFileTool.class);

    @Test
    public void readFile() throws Exception {

        // given
        File file = new File("C:/Temp/kit1.aif");
//        File file = ExampleFile.DRUM_PRESET_1.getFile();
        final AiffReader aiffReader = AiffReader.newAiffReader(file);

        // when
        final Aiff aiff = aiffReader.readAiff();
        ComplianceCheck.enforceCompliance(aiff);

        // then
        LOGGER.debug("aiff: %s", aiff);


    }

    @Test
    public void printJsonOfFile() throws Exception {

        // given
        final File file = new File("C:/Temp/kit1.aif");

        // when
        final String json = getJson(file);

        // then
        LOGGER.debug(String.format("json: %s", json));

    }

    @Test
    public void maxInteger() throws Exception {
        LOGGER.debug(String.format("min integer value: %s", Integer.MIN_VALUE));
        LOGGER.debug(String.format("max integer value: %s", Integer.MAX_VALUE));
    }

    @Test
    public void compareFiles() throws Exception {

        // given
        final File leftFile = new File("C:/Temp/kit1.aif");
        final File rightFile = new File("C:/Temp/1.aif");

        final DrumkitMeta leftDrumkitMeta = getDrumkitMeta(leftFile);
        final DrumkitMeta rightDrumkitMeta = getDrumkitMeta(rightFile);

        final Aiff leftAiff = readAiff(leftFile);
        final Aiff rightAiff = readAiff(rightFile);

        assertThat(leftAiff.getCommonChunk(), equalTo(rightAiff.getCommonChunk()));
        assertThat(leftAiff.getSoundDataChunk(), equalTo(rightAiff.getSoundDataChunk()));
        assertThat(leftAiff.getChunk(ChunkType.FORMAT_VERSION.getChunkId()), equalTo(rightAiff.getChunk(ChunkType.FORMAT_VERSION.getChunkId())));
//        assertThat(leftAiff.getChunk(ChunkType.APPLICATION.getChunkId()), equalTo(rightAiff.getChunk(ChunkType.APPLICATION.getChunkId())));

        final String leftJson = getJson(leftFile);
        final String rightJson = getJson(rightFile);
        assertThat(leftJson, equalTo(rightJson));

        LOGGER.debug(String.format("%s", leftJson));
        LOGGER.debug(String.format("%s", rightJson));

        assertTrue(FileUtils.contentEquals(leftFile, rightFile));
    }

    @Test
    public void printJsonFromUserFile() throws Exception {
        LOGGER.debug(String.format("json: %s", getJson(new File("C:/Temp/1.aif"))));
    }

    private DrumkitMeta getDrumkitMeta(File file) throws Exception {
        final ApplicationChunk chunk = getApplicationChunk(file);
        return DrumkitMeta.fromApplicationChunk(chunk);
    }

    private String getJson(File file) throws Exception {
        final ApplicationChunk chunk = getApplicationChunk(file);
        return chunk.getDataAsString();
    }

    private ApplicationChunk getApplicationChunk(File file) throws Exception {

        final AiffReader aiffReader = AiffReader.newAiffReader(file);
        final Aiff aiff = aiffReader.readAiff();
        ComplianceCheck.enforceCompliance(aiff);

        return (ApplicationChunk) aiff.getChunk(ChunkType.APPLICATION.getChunkId());
    }

    private Aiff readAiff(File file) throws Exception {

        final AiffReader aiffReader = AiffReader.newAiffReader(file);
        final Aiff aiff = aiffReader.readAiff();
        ComplianceCheck.enforceCompliance(aiff);

        return aiff;
    }
}
