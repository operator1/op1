package com.op1.aiff;

import java.io.File;
import java.net.URL;

public enum ExampleFile {
    ALBUM("Scrap30.aif"),
    ALBUM_SIDE_B("side_b.aif"),
    TAPE("track_1.aif"),
    DRUM_UTILITY("PO-12.aif"),
    DRUM_PRESET_1("1-drum.aif"),
    DRUM_PRESET_2("2-drum.aif"),
    DRUM_PRESET_3("3-drum.aif"),
    DRUM_PRESET_4("4-drum.aif"),
    DRUM_PRESET_5("5-drum.aif"),
    DRUM_PRESET_6("6-drum.aif"),
    DRUM_PRESET_7("7-drum.aif"),
    DRUM_PRESET_8("8-drum.aif"),
    SYNTH_PRESET("1-synth.aif");

    private final String classpathResource;

    ExampleFile(String classpathResource) {
        this.classpathResource = classpathResource;
    }

    public File getFile() {

        final URL resource = ExampleFile.class.getClassLoader().getResource(classpathResource);
        if (resource == null) {
            throw new IllegalArgumentException("Cannot find file: " + classpathResource);
        }

        return new File(resource.getFile());
    }
}
