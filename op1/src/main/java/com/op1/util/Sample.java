package com.op1.util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public final class Sample {

    private final File file;
    private final AudioFormat audioFormat;
    private final float duration;

    public Sample(File file, AudioFormat audioFormat, float duration) {
        this.file = file;
        this.audioFormat = audioFormat;
        this.duration = duration;
    }

    public File getFile() {
        return file;
    }

    public float getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "file=" + file +
                ", audioFormat=" + audioFormat +
                ", duration=" + duration +
                '}';
    }

    public static Sample fromFile(final File file) throws IOException, UnsupportedAudioFileException {

        final AudioFormat format = getAudioFormatOfFile(file);
        final AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
        final float duration = aff.getFrameLength() / format.getFrameRate();
        return new Sample(file, format, duration);
    }

    private static AudioFormat getAudioFormatOfFile(File wav) throws UnsupportedAudioFileException, IOException {
        final AudioInputStream audioInputStream = getAudioInputStream(wav);
        return audioInputStream.getFormat();
    }
}
