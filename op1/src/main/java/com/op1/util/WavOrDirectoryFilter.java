package com.op1.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioSystem.isConversionSupported;

public class WavOrDirectoryFilter implements FileFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WavOrDirectoryFilter.class);

    public boolean accept(File file) {
        return isReadableDirectory(file) ||
                (isReadableWavFile(file) && isTwelveSecondsOrLess(file) && isConversionToOp1FormatSupported(file));
    }

    private boolean isReadableWavFile(File pathname) {
        return pathname.isFile() && pathname.canRead() && pathname.getName().endsWith(".wav");
    }

    private boolean isTwelveSecondsOrLess(File file) {

        // Assume is readable wav
        try {
            AudioFormat audioFormat = getAudioFormatOfFile(file);
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);

            return hasSpecifiedFrameLength(fileFormat) && getDuration(audioFormat, fileFormat) < 12;

        } catch (Exception e) {
            return false;
        }
    }

    private boolean isConversionToOp1FormatSupported(File file) {

        final AudioFormat targetAudioFormat = new AudioFormat(PCM_SIGNED, 44100, 16, 1, 2, 2, false);
        final AudioFormat sourceAudioFormat;
        try {
            sourceAudioFormat = getAudioFormatOfFile(file);
        } catch (UnsupportedAudioFileException | IOException e) {
            LOGGER.error(String.format("Could not determine audio format of file %s", file.getPath()));
            return false;
        }
        return isConversionSupported(targetAudioFormat, sourceAudioFormat);
    }

    private float getDuration(AudioFormat audioFormat, AudioFileFormat fileFormat) {
        return fileFormat.getFrameLength() / audioFormat.getFrameRate();
    }

    private boolean hasSpecifiedFrameLength(AudioFileFormat fileFormat) {
        return fileFormat.getFrameLength() != AudioSystem.NOT_SPECIFIED;
    }

    private boolean isReadableDirectory(File pathname) {
        return pathname.isDirectory() && pathname.canRead();
    }

    private static AudioFormat getAudioFormatOfFile(File wav) throws UnsupportedAudioFileException, IOException {
        final AudioInputStream audioInputStream = getAudioInputStream(wav);
        return audioInputStream.getFormat();
    }
}
