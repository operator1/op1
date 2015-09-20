package com.op1.aiff;

import java.io.File;
import java.net.URL;

public enum ExampleFile {
    ALBUM("Scrap30.aif"),
    TAPE("track_1.aif");

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
