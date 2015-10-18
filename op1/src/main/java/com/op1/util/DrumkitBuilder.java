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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class DrumkitBuilder {

    private final SampleProvider sampleProvider;
    private final File targetDirectory;
    private final AtomicInteger counter = new AtomicInteger(0);

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

    public void buildKits() throws IOException, UnsupportedAudioFileException {

        final List<Item> items = representAsItems(sampleProvider.listSamples());
        final BinCompletionAlgorithm binCompletionAlgorithm = new BinCompletionAlgorithm(CAPACITY, items, MAX_ITEMS_PER_BIN);

        final List<Bin> bins = binCompletionAlgorithm.packBins();
        for (Bin bin : bins) {
            LOGGER.debug(String.format("%s %s", bin.getItems().size(), bin));
            writeDrumKitToTargetFolder(bin);
        }
    }

    private void writeDrumKitToTargetFolder(Bin bin) throws UnsupportedAudioFileException, IOException {

        final byte[] sampleData = getConvertedSampleData(bin);
        final SoundDataChunk soundDataChunk = buildSoundDataChunk(sampleData);
        final CommonChunk commonChunk = buildCommonChunk(sampleData);
        final FormatVersionChunk formatVersionChunk = Op1Constants.FORMAT_VERSION_CHUNK;
        final String json = DrumkitMeta.toJson(DrumkitMeta.newDefaultDrumkitMeta("kit"));
        final ApplicationChunk applicationChunk = buildApplicationChunk(json);
        final int aiffChunkSize = computeAiffChunkSize(soundDataChunk, commonChunk, formatVersionChunk, applicationChunk);
        final Aiff aiff = buildAiff(soundDataChunk, commonChunk, formatVersionChunk, applicationChunk, aiffChunkSize);

        checkCompliance(aiff);
        writeAiff(aiff);
    }

    private void writeAiff(Aiff aiff) throws IOException {
        final File targetFile = new File(targetDirectory, "kit" + counter.incrementAndGet() + ".aif");
        try (AiffWriter aiffWriter = AiffWriter.newAiffWriter(targetFile)) {
            aiffWriter.writeAiff(aiff);
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
        return new ApplicationChunk.Builder()
                .withChunkSize(SignedLong.fromInt(json.length() + 4)) // +4 for application signature
                .withApplicationSignature(Op1Constants.APPLICATION_CHUNK_SIGNATURE)
                .withData(SignedChar.fromString(json))
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
            new ComplianceCheck(aiff).enforceCompliance();
        } catch (AiffNotCompliantException e) {
            final String message = String.format("There are compliancy issues with this generated aiff: %s", aiff);
            throw new IllegalStateException(message, e);
        }
    }

    private byte[] getConvertedSampleData(Bin bin) throws UnsupportedAudioFileException, IOException {

        // TODO: set size of underlying byte array
        final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(bytesOut);

        for (Item item : bin.getItems()) {
            convertAndWriteBytes(new File(item.getName()), dataOutputStream);
        }

        final byte[] sampleData = bytesOut.toByteArray();
        Check.that(sampleData.length % 2 == 0, "Expected sample data to contain an even number of bytes");

        return sampleData;
    }

    private SoundDataChunk buildSoundDataChunk(byte[] sampleData) {
        return new SoundDataChunk.Builder()
                .withChunkSize(SignedLong.fromInt(sampleData.length + 8)) // +8 for offset and block size
                .withOffset(UnsignedLong.fromLong(0))
                .withBlockSize(UnsignedLong.fromLong(0))
                .withSampleData(sampleData)
                .build();
    }

    private void convertAndWriteBytes(File sourceFile, DataOutputStream dataOutputStream)
            throws UnsupportedAudioFileException, IOException {

        final AudioInputStream sourceAudioInputStream = getAudioInputStream(sourceFile);
        final AudioInputStream targetAudioInputStream = getAudioInputStream(TARGET_AUDIO_FORMAT, sourceAudioInputStream);

        byte[] buffer = new byte[1024];
        while (true) {
            final int numRead = targetAudioInputStream.read(buffer);
            if (numRead == -1) {
                break;
            }
            dataOutputStream.write(buffer, 0, numRead);
        }

        sourceAudioInputStream.close();
        targetAudioInputStream.close();
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

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        checkArgsLength(args);
        final File sourceDirectory = parseSourceDirectory(args);
        final File targetDirectory = parseTargetDirectory(args);
        final SampleProvider sampleProvider = new FileSystemSampleProvider(sourceDirectory, new WavOrDirectoryFilter());
        final DrumkitBuilder drumkitBuilder = new DrumkitBuilder(sampleProvider, targetDirectory);
        drumkitBuilder.buildKits();
    }

    private static void checkArgsLength(String[] args) {
        if (args.length != 4) {
            printUsageAndExit();
        }
    }

    private static File parseTargetDirectory(String[] args) {

        final String targetFlag = args[2];
        if (!targetFlag.equals("-target")) {
            printUsageAndExit();
        }

        final File directory = new File(args[3]);
        if (!directory.exists()) {
            System.err.println(String.format("Directory does not exist: %s", directory.getAbsolutePath()));
        }
        if (!directory.isDirectory()) {
            System.err.println(String.format("File is not a directory: %s", directory.getAbsolutePath()));
        }
        if (!directory.canWrite()) {
            System.err.println(String.format("Cannot write to directory: %s", directory.getAbsolutePath()));
        }
        return directory;
    }

    private static File parseSourceDirectory(String[] args) {

        final String sourceFlag = args[0];
        if (!sourceFlag.equals("-source")) {
            printUsageAndExit();
        }

        final File directory = new File(args[1]);
        if (!directory.exists()) {
            System.err.println(String.format("Directory does not exist: %s", directory.getAbsolutePath()));
        }
        if (!directory.isDirectory()) {
            System.err.println(String.format("File is not a directory: %s", directory.getAbsolutePath()));
        }
        if (!directory.canRead()) {
            System.err.println(String.format("Cannot read directory: %s", directory.getAbsolutePath()));
        }
        return directory;
    }

    private static void printUsageAndExit() {
        final String helpMessage = "java -jar op1util.jar -source <startdir> -target <targetdir>";
        System.err.println(helpMessage);
        System.exit(1);
    }
}
