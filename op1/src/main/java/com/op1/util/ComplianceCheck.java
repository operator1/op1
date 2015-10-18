package com.op1.util;

import com.op1.aiff.*;
import com.op1.iff.Chunk;
import com.op1.iff.types.ID;
import com.op1.iff.types.SignedLong;

import java.util.ArrayList;
import java.util.List;

import static com.op1.aiff.ChunkType.APPLICATION;
import static java.lang.String.format;

public class ComplianceCheck {

    private final Aiff aiff;
    private final List<String> problems;

    public ComplianceCheck(Aiff aiff) {
        this.aiff = Check.notNull(aiff, "aiff cannot be null");
        this.problems = new ArrayList<>();
    }

    public void enforceCompliance() throws AiffNotCompliantException {

        checkComplianceOfFormChunk();

        if (aiff.getFormType().toString().equals("AIFC")) {
            checkComplianceOfFormatVersionChunk();
        }

        checkComplianceOfCommonChunk();
        checkComplianceOfSoundDataChunk();

        if (aiff.hasChunk(APPLICATION)) {
            checkComplianceOfApplicationChunk();
        }

        if (!problems.isEmpty()) {
            throw new AiffNotCompliantException(problems);
        }
    }

    private void checkComplianceOfFormChunk() {

        int physicalSizeOfDataPortion = 0;

        physicalSizeOfDataPortion += aiff.getFormType().getSize();

        for (List<Chunk> chunksById : aiff.getChunksMap().values()) {
            for (Chunk chunk : chunksById) {
                physicalSizeOfDataPortion += chunk.getPhysicalSize();
            }
        }

        int expectedChunkSize = physicalSizeOfDataPortion;
        int expectedPhysicalSize = expectedChunkSize + 8;

        checkChunkSizes(aiff, expectedChunkSize, expectedPhysicalSize);
    }

    private void checkComplianceOfFormatVersionChunk() {

        final FormatVersionChunk chunk = (FormatVersionChunk) aiff.getChunk(ChunkType.FORMAT_VERSION.getChunkId());

        int expectedChunkSize = chunk.getTimestamp().getSize();
        int expectedPhysicalSize = expectedChunkSize + 8;

        checkChunkSizes(chunk, expectedChunkSize, expectedPhysicalSize);
    }

    private void checkComplianceOfApplicationChunk() {

        final ApplicationChunk chunk = (ApplicationChunk) aiff.getChunk(APPLICATION.getChunkId());
        final int applicationSignatureSize = chunk.getApplicationSignature().getSize();
        final int applicationDataSize = chunk.getData().length;
        final int expectedChunkSize = applicationSignatureSize + applicationDataSize;
        final int expectedPhysicalSize = expectedChunkSize + 8;

        checkChunkSizes(chunk, expectedChunkSize, expectedPhysicalSize);
    }

    private void checkComplianceOfCommonChunk() {

        final CommonChunk chunk = aiff.getCommonChunk();

        int physicalSizeOfDataPortion = 0;

        physicalSizeOfDataPortion += chunk.getNumChannels().getSize();
        physicalSizeOfDataPortion += chunk.getNumSampleFrames().getSize();
        physicalSizeOfDataPortion += chunk.getSampleSize().getSize();
        physicalSizeOfDataPortion += chunk.getSampleRate().getSize();

        if (codecAndDescriptionArePresent(chunk.getCodec(), chunk.getDescription())) {
            physicalSizeOfDataPortion += chunk.getCodec().getSize();
            physicalSizeOfDataPortion += chunk.getDescription().length;
        }

        int expectedChunkSize = physicalSizeOfDataPortion % 2 == 0 ? physicalSizeOfDataPortion : physicalSizeOfDataPortion + 1;
        int expectedPhysicalSize = physicalSizeOfDataPortion + 8;

        checkChunkSizes(chunk, expectedChunkSize, expectedPhysicalSize);
    }

    private void checkChunkSizes(Chunk chunk, int expectedChunkSize, int expectedPhysicalSize) {

        final String message = format("chunkSize in the %s chunk is %s, was expecting %s",
                chunk.getChunkID().toString(), chunk.getChunkSize().toInt(), expectedChunkSize);
        check(chunk.getChunkSize().equals(SignedLong.fromInt(expectedChunkSize)), message);

        final String physicalSizeMessage = String.format("physical size of %s chunk is %s, was expecting %s",
                chunk.getChunkID().toString(), chunk.getPhysicalSize(), expectedPhysicalSize);
        check(chunk.getPhysicalSize() == expectedPhysicalSize, physicalSizeMessage);

        final String evenNumberMessage = String.format("physical size of %s chunk is %s, was expecting an even number",
                chunk.getChunkID().toString(), chunk.getPhysicalSize());
        check(chunk.getPhysicalSize() % 2 == 0, evenNumberMessage);
    }

    private boolean codecAndDescriptionArePresent(ID codec, byte[] description) {
        if (codec != null && description != null) {
            return true;
        }
        final boolean eitherArePresent = codec != null || description != null;
        if (eitherArePresent) {
            problems.add("Expected both codec and description (when one is provided, the other must also be provided");
        } 
        
        return false;
    }

    private void checkComplianceOfSoundDataChunk() {

        final SoundDataChunk chunk = aiff.getSoundDataChunk();

        int physicalSizeOfDataPortion = 0;

        physicalSizeOfDataPortion += chunk.getBlockSize().getSize();
        physicalSizeOfDataPortion += chunk.getOffset().getSize();
        physicalSizeOfDataPortion += chunk.getSampleData().length;

        int expectedChunkSize = physicalSizeOfDataPortion;
        int expectedPhysicalSize = physicalSizeOfDataPortion + 8;

        checkChunkSizes(chunk, expectedChunkSize, expectedPhysicalSize);
    }

    private void check(boolean b, String problem) {
        if (!b) {
            problems.add(problem);
        }
    }
}
