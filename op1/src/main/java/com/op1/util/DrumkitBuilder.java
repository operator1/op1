package com.op1.util;

import com.op1.aiff.*;
import com.op1.drumkit.DrumkitMeta;
import com.op1.iff.types.ID;
import com.op1.iff.types.SignedChar;
import com.op1.iff.types.SignedLong;
import com.op1.iff.types.UnsignedLong;
import com.op1.pack.Bin;
import com.op1.pack.BinCompletionAlgorithm;
import com.op1.pack.Item;
import com.op1.util.cmd.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

/**
 * Utility for building drum kits for the Op-1.
 * This class is not thread safe, please use in a thread confined manner.
 */
public class DrumkitBuilder {

    private final SampleProvider sampleProvider;
    private final File targetDirectory;
    private final List<SampleMeta> sampleMetaList = new ArrayList<>(24);

    private static final double CAPACITY = 12.0;
    private static final int MAX_ITEMS_PER_BIN = 24;
    private static final AudioFormat TARGET_AUDIO_FORMAT = new AudioFormat(PCM_SIGNED, 44100, 16, 1, 2, 2, false);
    private static final Logger LOGGER = LoggerFactory.getLogger(DrumkitBuilder.class);

    public DrumkitBuilder(final SampleProvider sampleProvider, File targetDirectory) throws IOException, UnsupportedAudioFileException {

        this.sampleProvider = Check.notNull(sampleProvider, "sampleProvider is null");

        Check.that(targetDirectory.isDirectory(), "Not a directory: " + targetDirectory.getAbsolutePath());
        Check.that(targetDirectory.canWrite(), "Cannot write to directory: " + targetDirectory.getAbsolutePath());
        this.targetDirectory = targetDirectory;
    }

    public void buildKits(String baseName) throws IOException, UnsupportedAudioFileException {

        final List<Item> items = representAsItems(sampleProvider.listSamples());
        final BinCompletionAlgorithm binCompletionAlgorithm = new BinCompletionAlgorithm(CAPACITY, items, MAX_ITEMS_PER_BIN);

        final List<Bin> bins = binCompletionAlgorithm.packBins();
        int counter = 0;
        for (Bin bin : bins) {
            writeDrumKitToTargetFolder(bin, baseName + ++counter);
        }
    }

    private void writeDrumKitToTargetFolder(Bin bin, String kitName) throws UnsupportedAudioFileException, IOException {

        sampleMetaList.clear();
        final byte[] sampleData = getConvertedSampleData(bin);
        final SoundDataChunk soundDataChunk = buildSoundDataChunk(sampleData);
        final CommonChunk commonChunk = buildCommonChunk(sampleData);
        final FormatVersionChunk formatVersionChunk = Op1Constants.FORMAT_VERSION_CHUNK;
        final DrumkitMeta.Builder drumkitMetaBuilder = DrumkitMeta.newDefaultDrumkitMetaBuilder(kitName);
        hackBuilder(drumkitMetaBuilder);
        addSampleStartAndEndPoints(drumkitMetaBuilder);
        final String json = DrumkitMeta.toJson(drumkitMetaBuilder.build());
        final ApplicationChunk applicationChunk = buildApplicationChunk(json + new String(new byte[] {10, 32}));
        final int aiffChunkSize = computeAiffChunkSize(soundDataChunk, commonChunk, formatVersionChunk, applicationChunk);
        final Aiff aiff = buildAiff(soundDataChunk, commonChunk, formatVersionChunk, applicationChunk, aiffChunkSize);

        checkCompliance(aiff);
        writeAiff(aiff, kitName, bin);
    }

    private void hackBuilder(DrumkitMeta.Builder builder) {

        int[] fxParams = new int[8];
        Arrays.fill(fxParams, 8000);
        builder.withFxParams(fxParams);

        int[] lfoParams = new int[8];
        Arrays.fill(lfoParams, 16000);
        builder.withLfoParams(lfoParams);

        int[] playMode = new int[24];
        Arrays.fill(playMode, 12287);
//        Arrays.fill(playMode, 16, 24, 5119);
        builder.withPlaymode(playMode);

        int[] reverse = new int[24];
        Arrays.fill(reverse, 12000);
        builder.withReverse(reverse);
    }

    private void addSampleStartAndEndPoints(DrumkitMeta.Builder builder) {

        int[] start = new int[24];
        int[] end = new int[24];
        for (int i = 0; i < sampleMetaList.size(); i++) {
            start[i] = convertNumBytesInSampleToOp1SamplePoint(sampleMetaList.get(i).start);
            end[i] = convertNumBytesInSampleToOp1SamplePoint(sampleMetaList.get(i).end);
        }
        builder.withStart(start).withEnd(end);
    }

    private void writeAiff(Aiff aiff, String kitName, Bin bin) throws IOException {
        final int numSamples = bin.getItems().size();
        final File targetFile = new File(targetDirectory, kitName + ".aif");
        try (AiffWriter aiffWriter = AiffWriter.newAiffWriter(targetFile)) {
            aiffWriter.writeAiff(aiff);
            LOGGER.debug(String.format("Created kit of %s samples and length %.2f seconds: %s", numSamples, bin.size(), targetFile.getAbsolutePath()));
        }
    }

    private Aiff buildAiff(SoundDataChunk soundDataChunk, CommonChunk commonChunk, FormatVersionChunk formatVersionChunk, ApplicationChunk applicationChunk, int aiffChunkSize) {
        return new Aiff.Builder()
                .withChunkId(ID.valueOf("FORM"))
                .withChunk(ChunkType.FORMAT_VERSION.getChunkId(), formatVersionChunk)
                .withChunk(ChunkType.COMMON.getChunkId(), commonChunk)
                .withChunk(ChunkType.APPLICATION.getChunkId(), applicationChunk)
                .withChunk(ChunkType.SOUND_DATA.getChunkId(), soundDataChunk)
                .withFormType(ID.valueOf("AIFC"))
                .withChunkSize(SignedLong.fromInt(aiffChunkSize))
                .build();
    }

    private int computeAiffChunkSize(SoundDataChunk soundDataChunk, CommonChunk commonChunk, FormatVersionChunk formatVersionChunk, ApplicationChunk applicationChunk) {
        return formatVersionChunk.getPhysicalSize()
                + commonChunk.getPhysicalSize()
                + applicationChunk.getPhysicalSize()
                + soundDataChunk.getPhysicalSize() + 4; // +4 for form type
    }

    private ApplicationChunk buildApplicationChunk(String json) {

        final SignedChar[] data = SignedChar.fromString(json);

        return new ApplicationChunk.Builder()
                .withChunkSize(SignedLong.fromInt(json.length() + 4)) // +4 for application signature
                .withApplicationSignature(Op1Constants.APPLICATION_CHUNK_SIGNATURE)
                .withData(data)
                .build();
    }

    private CommonChunk buildCommonChunk(byte[] sampleData) {
        return new CommonChunk.Builder()
                .withChunkSize(SignedLong.fromInt(64))
                .withNumChannels(Op1Constants.NUM_CHANNELS_MONO)
                .withSampleRate(Op1Constants.SAMPLE_RATE_44100)
                .withSampleSize(Op1Constants.SAMPLE_SIZE_16_BIT)
                .withNumSampleFrames(UnsignedLong.fromLong(sampleData.length / 2))
                .withCodec(Op1Constants.ID_SOWT)
                .withDescription(Op1Constants.COMMON_CHUNK_DESCRIPTION)
                .build();
    }

    private void checkCompliance(Aiff aiff) {
        try {
            ComplianceCheck.enforceCompliance(aiff);
        } catch (AiffNotCompliantException e) {
            final String message = String.format("There are compliancy issues with this generated aiff: %s", aiff);
            throw new IllegalStateException(message, e);
        }
    }

    private byte[] getConvertedSampleData(Bin bin) throws UnsupportedAudioFileException, IOException {

        final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(bytesOut);

        SampleMeta lastSampleMeta = null;
        for (Item item : bin.getItems()) {
            final SampleMeta sampleMeta = convertAndWriteBytes(new File(item.getName()), dataOutputStream, lastSampleMeta);
            sampleMetaList.add(sampleMeta);
            lastSampleMeta = sampleMeta;
        }

        final int totalBytesForAllSamples = calculateTotalBytesForAllSamples();
        final int numBytesToPad = (44100 * 2 * 12) - totalBytesForAllSamples;
        final byte[] padBytes = new byte[numBytesToPad];
        Arrays.fill(padBytes, (byte) 0);
        dataOutputStream.write(padBytes, 0, padBytes.length);

        final byte[] sampleData = bytesOut.toByteArray();
        Check.that(sampleData.length == (44100 * 2 * 12), "Expected sample data to contain 1058400 bytes (num bytes of 44100 16 bit audio in 12 seconds");

        return sampleData;
    }

    private int calculateTotalBytesForAllSamples() {
        int total = 0;
        for (SampleMeta sampleMeta : sampleMetaList) {
            total += sampleMeta.numBytes;
        }
        return total;
    }

    private SoundDataChunk buildSoundDataChunk(byte[] sampleData) {
        return new SoundDataChunk.Builder()
                .withChunkSize(SignedLong.fromInt(sampleData.length + 8)) // +8 for offset and block size
                .withOffset(UnsignedLong.fromLong(0))
                .withBlockSize(UnsignedLong.fromLong(0))
                .withSampleData(sampleData)
                .build();
    }

    private SampleMeta convertAndWriteBytes(File sourceFile, DataOutputStream dataOutputStream, SampleMeta lastMeta)
            throws UnsupportedAudioFileException, IOException {

        final AudioInputStream sourceAudioInputStream = getAudioInputStream(sourceFile);
        final AudioInputStream targetAudioInputStream = getAudioInputStream(TARGET_AUDIO_FORMAT, sourceAudioInputStream);

        byte[] buffer = new byte[1024];
        int numBytesInSample = 0;
        while (true) {
            final int numRead = targetAudioInputStream.read(buffer);
            if (numRead == -1) {
                break;
            }
            dataOutputStream.write(buffer, 0, numRead);
            numBytesInSample += numRead;
        }

        sourceAudioInputStream.close();
        targetAudioInputStream.close();

        if (lastMeta == null) {
            return new SampleMeta(0, numBytesInSample, numBytesInSample);
        } else {
            final int start = lastMeta.end + 1;
            return new SampleMeta(start, start + numBytesInSample, numBytesInSample);
        }
    }

    private int convertNumBytesInSampleToOp1SamplePoint(int numBytesInSample) {
        final int numBytesInTwelveSeconds = 44100 * 2 * 12;
        return Op1Constants.DRUMKIT_END / numBytesInTwelveSeconds * numBytesInSample;
    }

    private List<Item> representAsItems(List<Sample> samples) throws IOException, UnsupportedAudioFileException {
        List<Item> items = new ArrayList<>(samples.size());
        for (Sample sample : samples) {
            items.add(representAsItem(sample));
        }
        return items;
    }

    private Item representAsItem(Sample sample) throws IOException, UnsupportedAudioFileException {
        return new Item(sample.getFile().getPath(), (double) sample.getDuration());
    }

    private static class SampleMeta {

        private final int start;
        private final int end;
        private final int numBytes;

        private SampleMeta(int start, int end, int numBytes) {
            this.start = start;
            this.end = end;
            this.numBytes = numBytes;
        }
    }

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {

        // TODO: Total crap that we're specifying the full jar name here.
        final Usage usage = Usage.newBuilder()
                .programName("java -jar op1-drumkit-1.0.0-SNAPSHOT.jar")
                .mandatory("-source", "the root folder containing samples")
                .mandatory("-target", "the target folder to write drumkits to")
                .optional("-baseName", "the base name of the drumkit (numbers will be appended to this name)")
                .optional("-maxLength", "the maximum length of a sample to include in a kit").build();

        final Options options = usage.parse(new Args(args));
        final File sourceDirectory = options.getValue("-source", new SourceDirectoryValidator());
        final File targetDirectory = options.getValue("-target", new TargetDirectoryValidator());
        final FileFilter fileFilter = options.getValue("-maxLength", new FileFilterValidator(), new WavOrDirectoryFilter());
        final String baseName = options.getValue("-baseName", new BaseNameValidator(), "kit");

        final SampleProvider sampleProvider = new FileSystemTreeSampleProvider(sourceDirectory, fileFilter);
        final DrumkitBuilder drumkitBuilder = new DrumkitBuilder(sampleProvider, targetDirectory);
        drumkitBuilder.buildKits(baseName);
    }

    private static class SourceDirectoryValidator implements Validator<File> {

        @Override
        public File validate(String sourceDir) throws ValidationException {

            final File directory = new File(sourceDir);
            if (!directory.exists()) {
                throw new ValidationException(String.format("Directory does not exist: %s", directory.getAbsolutePath()));
            }
            if (!directory.isDirectory()) {
                throw new ValidationException(String.format("File is not a directory: %s", directory.getAbsolutePath()));
            }
            if (!directory.canRead()) {
                throw new ValidationException(String.format("Cannot read directory: %s", directory.getAbsolutePath()));
            }
            return directory;
        }
    }

    private static class TargetDirectoryValidator implements Validator<File> {

        @Override
        public File validate(String targetDir) throws ValidationException {

            final File directory = new File(targetDir);
            if (!directory.exists()) {
                final boolean success = directory.mkdirs();
                if (!success) {
                    throw new ValidationException(String.format("Had problems creating target directory: %s", directory.getAbsolutePath()));
                }
            }
            if (!directory.isDirectory()) {
                throw new ValidationException(String.format("File is not a directory: %s", directory.getAbsolutePath()));
            }
            if (!directory.canWrite()) {
                throw new ValidationException(String.format("Cannot write to directory: %s", directory.getAbsolutePath()));
            }
            return directory;
        }
    }

    private static class FileFilterValidator implements Validator<FileFilter> {
        @Override
        public FileFilter validate(String option) throws ValidationException {
            try {
                Float maxLength = Float.parseFloat(option);
                return new WavOrDirectoryFilter(maxLength);
            } catch (NumberFormatException e) {
                throw new ValidationException("Expected a floating point number, but got: " + option);
            }
        }
    }

    private static class BaseNameValidator implements Validator<String> {
        @Override
        public String validate(String option) throws ValidationException {
            return option;
        }
    }
}
