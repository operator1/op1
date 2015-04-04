package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.IffWriter;
import com.op1.iff.types.ID;
import com.op1.iff.types.SignedChar;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class AiffWriter {

    private final IffWriter writer;

    public AiffWriter(IffWriter writer) {
        this.writer = writer;
    }

    public void writeAiff(Aiff aiff) throws IOException {

        // header
        writer.write(aiff.getChunkID());
        writer.write(aiff.getChunkSize());
        writer.write(aiff.getFormat());

        // chunks
        final Set<Map.Entry<ID, Chunk>> entries = aiff.getChunks().entrySet();
        for (Map.Entry<ID, Chunk> entry : entries) {
            final Chunk chunk = entry.getValue();
            if (chunk.getChunkID().equals(ChunkType.COMMON.getId())) {
                writeCommonChunk((CommonChunk) chunk);
            } else if (chunk.getChunkID().equals(ChunkType.SOUND_DATA.getId())) {
                writeSoundDataChunk((SoundDataChunk) chunk);
            } else if (chunk.getChunkID().equals(ChunkType.APPLICATION.getId())) {
                writeApplicationChunk((ApplicationChunk) chunk);
            } else if (chunk.getChunkID().equals(ChunkType.MARKER.getId())) {
                writeMarkerChunk((MarkerChunk) chunk);
            } else if (chunk.getChunkID().equals(ChunkType.INSTRUMENT.getId())) {
                writeInstrumentChunk((InstrumentChunk) chunk);
            } else {
                writeUnknownChunk((UnknownChunk) chunk);
            }
        }
    }

    private void writeCommonChunk(CommonChunk commonChunk) throws IOException {
        writer.write(commonChunk.getChunkID());
        writer.write(commonChunk.getChunkSize());
        writer.write(commonChunk.getNumChannels());
        writer.write(commonChunk.getNumSampleFrames());
        writer.write(commonChunk.getSampleSize());
        writer.write(commonChunk.getSampleRate());

        if (commonChunk.getCodec() != null) {
            writer.write(commonChunk.getCodec());
            writer.write(commonChunk.getDescription());
        }
    }

    private void writeSoundDataChunk(SoundDataChunk chunk) throws IOException {
        writer.write(chunk.getChunkID());
        writer.write(chunk.getChunkSize());
        writer.write(chunk.getOffset());
        writer.write(chunk.getBlockSize());
        writer.write(chunk.getSampleData());
    }

    private void writeApplicationChunk(ApplicationChunk chunk) throws IOException {
        writer.write(chunk.getChunkID());
        writer.write(chunk.getChunkSize());
        writer.write(chunk.getApplicationSignature());

        for (SignedChar signedChar : chunk.getData()) {
            writer.write(signedChar);
        }
    }

    private void writeMarkerChunk(MarkerChunk chunk) throws IOException {
        writer.write(chunk.getChunkID());
        writer.write(chunk.getChunkSize());
        writer.write(chunk.getNumMarkers());

        for (MarkerChunk.Marker marker : chunk.getMarkers()) {
            writeMarker(marker);
        }
    }

    private void writeMarker(MarkerChunk.Marker marker) throws IOException {
        writer.write(marker.getMarkerId());
        writer.write(marker.getPosition());
        writer.write(marker.getMarkerName());
    }

    private void writeInstrumentChunk(InstrumentChunk chunk) throws IOException {
        writer.write(chunk.getChunkID());
        writer.write(chunk.getChunkSize());
        writer.write(chunk.getBaseNote());
        writer.write(chunk.getDetune());
        writer.write(chunk.getLowNote());
        writer.write(chunk.getHighNote());
        writer.write(chunk.getLowVelocity());
        writer.write(chunk.getHighVelocity());
        writer.write(chunk.getGain());
        writeInstrumentLoop(chunk.getSustainLoop());
        writeInstrumentLoop(chunk.getReleaseLoop());
    }

    private void writeInstrumentLoop(InstrumentChunk.Loop loop) throws IOException {
        writer.write(loop.getPlayMode());
        writer.write(loop.getBeginLoop());
        writer.write(loop.getEndLoop());
    }

    private void writeUnknownChunk(UnknownChunk chunk) throws IOException {
        writer.write(chunk.getChunkID());
        writer.write(chunk.getChunkSize());
        writer.write(chunk.getChunkData());
    }
}
