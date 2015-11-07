package com.op1.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public class DrumkitBuilderTool {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String START_DIRECTORY = "C:\\home\\audio\\library\\Roland Drum Samples";

    @Test
    public void buildKits() throws Exception {

        final File startDirectory = new File(START_DIRECTORY);
        final File targetDirectory = new File("C:\\Temp");
        final FileSystemTreeSampleProvider fileSystemTreeSampleProvider = new FileSystemTreeSampleProvider(startDirectory, new WavOrDirectoryFilter());
        final DrumkitBuilder drumkitBuilder = new DrumkitBuilder(fileSystemTreeSampleProvider, targetDirectory);

        drumkitBuilder.buildKits("kit");
    }

    @Test
    public void buildKitFromFileList() throws Exception {

        // given
        final File folder = new File("C:\\home\\audio\\library\\Roland Drum Samples\\Boss DR-202");
        final SampleProvider sampleProvider = new SameDirectorySampleProvider(folder, "202cla01.wav", "202cla02.wav",
                "202cla03.wav", "202cla04.wav", "202cla05.wav", "202cla06.wav", "202cla07.wav", "202cla08.wav",
                "202cla09.wav", "202cla10.wav", "202cla11.wav", "202cla12.wav");

        new DrumkitBuilder(sampleProvider, new File("C:\\Temp")).buildKits("kit");
    }
}