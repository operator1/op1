package com.op1.util;

import com.op1.aiff.Aiff;
import com.op1.aiff.ChunkType;
import com.op1.aiff.CommonChunk;
import com.op1.aiff.SoundDataChunk;
import com.op1.iff.Chunk;
import com.op1.iff.types.ID;
import com.op1.iff.types.SignedLong;
import com.op1.iff.types.SignedShort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChannelSplitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelSplitter.class);

    public Aiff[] splitChannels(Aiff aiff) {

        Check.notNull(aiff, "Aiff is null");

        final short numChannels = aiff.getCommonChunk().getNumChannels().toShort();
        final short sampleSize = aiff.getCommonChunk().getSampleSize().toShort();
        final int bytesPerSample = sampleSize / 8;
        final byte[] sampleData = aiff.getSoundDataChunk().getSampleData();
        LOGGER.debug(String.format("Original file sample data size: %s", sampleData.length));
        final byte[][] bytesByChannel = splitSampleDataByChannel(numChannels, bytesPerSample, sampleData);

        return getAiffsByChannel(aiff, numChannels, sampleData, bytesByChannel);
    }

    private Aiff[] getAiffsByChannel(Aiff aiff, short numChannels, byte[] sampleData, byte[][] bytesByChannel) {

        Aiff[] aiffs = new Aiff[numChannels];
        for (int i = 0; i < numChannels; i++) {

            int aiffChunkSize = 12;

            final Aiff.Builder builder = new Aiff.Builder()
                    .withChunkId(aiff.getChunkID())
                    .withFormType(aiff.getFormType());

            final Set<Map.Entry<ID, List<Chunk>>> entries = aiff.getChunksMap().entrySet();
            for (Map.Entry<ID, List<Chunk>> entry : entries) {
                final List<Chunk> chunks = entry.getValue();
                for (Chunk chunk : chunks) {
                    if (chunk.getChunkID().equals(ChunkType.SOUND_DATA.getChunkId())) {
                        SoundDataChunk soundChunk = modifySoundDataChunk(aiff, sampleData, bytesByChannel[i], numChannels);
                        aiffChunkSize += soundChunk.getPhysicalSize();
                        builder.withChunk(ChunkType.SOUND_DATA.getChunkId(), soundChunk);

                    } else if (chunk.getChunkID().equals(ChunkType.COMMON.getChunkId())) {
                        CommonChunk commonChunk = modifyCommonChunkToHaveOneChannel(aiff);
                        aiffChunkSize += commonChunk.getPhysicalSize();
                        LOGGER.debug(String.format("Split channel: %s", commonChunk.toString()));
                        builder.withChunk(ChunkType.COMMON.getChunkId(), commonChunk);

                    } else {
                        builder.withChunk(chunk.getChunkID(), chunk);
                        aiffChunkSize += chunk.getPhysicalSize();
                    }
                }
            }

            builder.withChunkSize(SignedLong.fromInt(aiffChunkSize));
            aiffs[i] = builder.build();
        }
        return aiffs;
    }

    private SoundDataChunk modifySoundDataChunk(Aiff aiff, byte[] sampleData, byte[] channelBytes, short numChannels) {
        LOGGER.debug(String.format("Split file sample data length: %s", channelBytes.length));
        return new SoundDataChunk.Builder()
                .withChunkSize(SignedLong.fromInt((sampleData.length / numChannels) + 8))
                .withOffset(aiff.getSoundDataChunk().getOffset())
                .withBlockSize(aiff.getSoundDataChunk().getBlockSize())
                .withSampleData(channelBytes)
                .build();
    }

    private CommonChunk modifyCommonChunkToHaveOneChannel(Aiff aiff) {

        CommonChunk original = aiff.getCommonChunk();

        final CommonChunk.Builder commonChunkBuilder = new CommonChunk.Builder()
                .withChunkSize(original.getChunkSize())
                .withNumChannels(SignedShort.fromShort((short) 1))
                .withNumSampleFrames(original.getNumSampleFrames())
                .withSampleSize(original.getSampleSize())
                .withSampleRate(original.getSampleRate());

        if (original.getCodec() != null) {
            commonChunkBuilder
                    .withCodec(original.getCodec())
                    .withDescription(original.getDescription());
        }

        return commonChunkBuilder.build();
    }

    private byte[][] splitSampleDataByChannel(short numChannels, int bytesPerSample, byte[] sampleData) {

        byte[][] bytesByChannel = new byte[numChannels][sampleData.length / numChannels];

        for (int i = 0; i < sampleData.length; i++) {
            byte b = sampleData[i];
            int channel = calculateChannel(i, numChannels, bytesPerSample);
            int offset = calculateOffset(i, numChannels, bytesPerSample);
            bytesByChannel[channel][offset] = b;
        }

        return bytesByChannel;
    }

    private int calculateChannel(int sampleIndex, short numChannels, int bytesPerSample) {
        return sampleIndex / bytesPerSample % numChannels;
    }

    private int calculateOffset(int sampleIndex, short numChannels, int bytesPerSample) {
        return (sampleIndex % bytesPerSample) + ((sampleIndex / (numChannels * bytesPerSample) * bytesPerSample));
    }
}
