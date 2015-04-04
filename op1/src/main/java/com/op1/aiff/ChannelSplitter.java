package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.types.ID;
import com.op1.iff.types.SignedLong;
import com.op1.iff.types.SignedShort;
import com.op1.util.Check;

import java.util.Map;
import java.util.Set;

public class ChannelSplitter {

    public Aiff[] splitChannels(Aiff aiff) {

        Check.notNull(aiff, "Aiff is null");

        final short numChannels = aiff.getCommonChunk().getNumChannels().toShort();
        final short sampleSize = aiff.getCommonChunk().getSampleSize().toShort();
        final int bytesPerSample = sampleSize / 8;
        final byte[] sampleData = aiff.getSoundDataChunk().getSampleData();
        final byte[][] bytesByChannel = splitSampleDataByChannel(numChannels, bytesPerSample, sampleData);

        return getAiffsByChannel(aiff, numChannels, sampleData, bytesByChannel);
    }

    private Aiff[] getAiffsByChannel(Aiff aiff, short numChannels, byte[] sampleData, byte[][] bytesByChannel) {

        Aiff[] aiffs = new Aiff[numChannels];
        for (int i = 0; i < numChannels; i++) {
            final Aiff.Builder builder = new Aiff.Builder()
                    .withChunkId(aiff.getChunkID())
                    .withChunkSize(aiff.getChunkSize())
                    .withFormat(aiff.getFormat());

            final Set<Map.Entry<ID, Chunk>> entries = aiff.getChunks().entrySet();
            for (Map.Entry<ID, Chunk> entry : entries) {
                final Chunk chunk = entry.getValue();
                if (chunk.getChunkID().equals(ChunkType.SOUND_DATA.getId())) {
                    final SoundDataChunk soundDataChunk = new SoundDataChunk.Builder()
                            .withChunkSize(SignedLong.fromInt(sampleData.length / numChannels))
                            .withOffset(aiff.getSoundDataChunk().getOffset())
                            .withBlockSize(aiff.getSoundDataChunk().getBlockSize())
                            .withSampleData(bytesByChannel[i])
                            .build();
                    builder.withChunk(ChunkType.SOUND_DATA.getId(), soundDataChunk);

                } else if (chunk.getChunkID().equals(ChunkType.COMMON.getId())) {
                    final CommonChunk.Builder commonChunkBuilder = new CommonChunk.Builder()
                            .withChunkSize(aiff.getCommonChunk().getChunkSize())
                            .withNumChannels(SignedShort.fromShort((short) 1))
                            .withNumSampleFrames(aiff.getCommonChunk().getNumSampleFrames())
                            .withSampleSize(aiff.getCommonChunk().getSampleSize())
                            .withSampleRate(aiff.getCommonChunk().getSampleRate());
                    if (aiff.getCommonChunk().getCodec() != null) {
                        commonChunkBuilder
                                .withCodec(aiff.getCommonChunk().getCodec())
                                .withDescription(aiff.getCommonChunk().getDescription());
                    }
                    builder.withChunk(ChunkType.COMMON.getId(), commonChunkBuilder.build());
                } else {
                    builder.withChunk(chunk.getChunkID(), chunk);
                }
            }

            aiffs[i] = builder.build();
        }
        return aiffs;
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
