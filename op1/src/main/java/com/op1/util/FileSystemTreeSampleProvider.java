package com.op1.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileSystemTreeSampleProvider implements SampleProvider {

    private final File startDirectory;
    private final FileFilter fileFilter;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemTreeSampleProvider.class);

    public FileSystemTreeSampleProvider(final File startDirectory, final FileFilter fileFilter) {
        this.startDirectory = checkIsReadableDirectory(startDirectory);
        this.fileFilter = fileFilter;
    }

    @Override
    public List<Sample> listSamples() {

        final List<Sample> samples = new ArrayList<>();
        findSamples(startDirectory, samples);
        return samples;
    }

    private void findSamples(File file, List<Sample> samples) {
        if (file.isDirectory()) {
            recurse(file, samples);
        } else {
            try {
                samples.add(Sample.fromFile(file));
            } catch (IOException | UnsupportedAudioFileException e) {
                LOGGER.error(String.format("Could not obtain meta data for file: %s", file.getPath()));
            }
        }
    }

    private void recurse(File file, List<Sample> wavs) {
        for (File f : file.listFiles(fileFilter)) {
            findSamples(f, wavs);
        }
    }

    private static File checkIsReadableDirectory(File file) {
        if (!file.isDirectory() || !file.canRead()) {
            throw new IllegalArgumentException(file.getAbsolutePath() + " is not a readable directory");
        }
        return file;
    }
}
