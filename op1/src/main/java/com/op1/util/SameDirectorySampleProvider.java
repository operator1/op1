package com.op1.util;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SameDirectorySampleProvider implements SampleProvider {

    private final List<Sample> samples;

    public SameDirectorySampleProvider(File folder, String... filenames) throws IOException, UnsupportedAudioFileException {

        Check.notNull(filenames, "filenames is null");
        Check.that(filenames.length <= 24, "You can have at most 24 samples in an Op-1 drumkit");

        this.samples = new ArrayList<>(24);

        for (String filename : filenames) {
            samples.add(Sample.fromFile(new File(folder, filename)));
        }
    }

    @Override
    public List<Sample> listSamples() {
        return samples;
    }
}
