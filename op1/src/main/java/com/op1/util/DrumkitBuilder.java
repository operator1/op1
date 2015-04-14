package com.op1.util;

import com.op1.pack.Bin;
import com.op1.pack.BinCompletionAlgorithm;
import com.op1.pack.Item;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DrumkitBuilder {

    private File startDirectory;

    public DrumkitBuilder(File startDirectory) throws IOException, UnsupportedAudioFileException {
        Check.that(startDirectory.isDirectory(), "Not a directory");
        Check.that(startDirectory.canRead(), "Cannot read directory");
        this.startDirectory = startDirectory;
    }

    public void findSamples() throws IOException, UnsupportedAudioFileException {

        List<File> wavs = new ArrayList<File>();
        doFindSamples(startDirectory, wavs);

        List<Item> items = representAsItems(wavs);
        final List<Bin> bins = new BinCompletionAlgorithm(12.0, items).packBins();
        for (Bin bin : bins) {
            System.out.println(bin);
        }
    }

    private List<Item> representAsItems(List<File> wavs) throws IOException, UnsupportedAudioFileException {
        List<Item> items = new ArrayList<Item>(wavs.size());
        for (File wav : wavs) {
            items.add(representAsItem(wav));
        }
        return items;
    }

    private Item representAsItem(File wav) throws IOException, UnsupportedAudioFileException {

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wav);
        AudioFormat format = audioInputStream.getFormat();
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(wav);

        double duration = aff.getFrameLength() / format.getFrameRate();
        return new Item(wav.getName(), duration);
    }

    private void doFindSamples(File file, List<File> wavs) throws IOException, UnsupportedAudioFileException {
        if (file.isDirectory()) {
            recurse(file, wavs);
        } else {
            printFormat(file);
            wavs.add(file);
        }
    }

    private void recurse(File file, List<File> wavs) throws IOException, UnsupportedAudioFileException {
        File[] files = file.listFiles(new WavOrDirectoryFilter());
        for (File f : files) {
            doFindSamples(f, wavs);
        }
    }

    private void printFormat(File file) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);

        float duration = aff.getFrameLength() / format.getFrameRate();
        System.out.println(String.format("%s - %s (%s seconds)", file.getName(), format, duration));
    }

    private static class WavOrDirectoryFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            return isReadableDirectory(pathname) || (isReadableWavFile(pathname) && isTwelveSecondsOrLess(pathname));
        }

        private boolean isReadableWavFile(File pathname) {
            return pathname.isFile() && pathname.canRead() && pathname.getName().endsWith(".wav");
        }

        private boolean isTwelveSecondsOrLess(File file) {

            // Assume is readable wav
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat audioFormat = audioInputStream.getFormat();
                AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);

                return hasSpecifiedFrameLength(fileFormat) && getDuration(audioFormat, fileFormat) < 1;

            } catch (Exception e) {
                return false;
            }
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
    }

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        String path = "C:\\home\\cav\\audio\\samples\\libraries";
        new DrumkitBuilder(new File(path)).findSamples();
    }
}
