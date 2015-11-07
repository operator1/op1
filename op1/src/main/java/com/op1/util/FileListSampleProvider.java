package com.op1.util;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileListSampleProvider implements SampleProvider {

    private final List<Sample> samples;

    public FileListSampleProvider(File... files) throws IOException, UnsupportedAudioFileException {

        Check.notNull(files, "files is null");
        Check.that(files.length <= 24, "You can have at most 24 samples in an Op-1 drumkit");

        this.samples = new ArrayList<>(24);

        for (File file : files) {
            samples.add(Sample.fromFile(file));
        }
    }

    @Override
    public List<Sample> listSamples() {
        return samples;
    }
}
