package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.IffReader;
import com.op1.iff.types.*;
import com.op1.util.Check;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarkerChunk implements Chunk {

    private final ID chunkId = ChunkType.MARKER.getId();
    private SignedLong chunkSize;
    private UnsignedShort numMarkers;
    private List<Marker> markers = new ArrayList<Marker>();

    private MarkerChunk() {
    }

    private MarkerChunk(MarkerChunk chunk) {
        this.chunkSize = chunk.getChunkSize();
        this.numMarkers = chunk.getNumMarkers();
        Collections.addAll(this.markers, chunk.getMarkers());
    }

    @Override
    public ID getChunkID() {
        return chunkId;
    }

    public SignedLong getChunkSize() {
        return chunkSize;
    }

    public UnsignedShort getNumMarkers() {
        return numMarkers;
    }

    public Marker[] getMarkers() {
        return markers.toArray(new Marker[markers.size()]);
    }

    public static class Marker {

        private final SignedShort markerId;
        private final UnsignedLong position;
        private final PString markerName;

        public Marker(SignedShort markerId, UnsignedLong position, PString markerName) {
            Check.notNull(markerId, "Missing markerId");
            Check.notNull(position, "Missing position");
            Check.notNull(markerName, "Missing markerName");
            this.markerId = markerId;
            this.position = position;
            this.markerName = markerName;
        }

        public SignedShort getMarkerId() {
            return markerId;
        }

        public UnsignedLong getPosition() {
            return position;
        }

        public PString getMarkerName() {
            return markerName;
        }
    }

    public static class Builder {

        private final MarkerChunk instance;

        public Builder() {
            instance = new MarkerChunk();
        }

        public MarkerChunk build() {
            Check.notNull(instance.chunkSize, "Missing chunkSize");
            Check.notNull(instance.numMarkers, "Missing numMarkers");
            Check.that(instance.markers.size() == instance.numMarkers.toInt(), "Mismatch between markers and numMarkers");
            return new MarkerChunk(instance);
        }

        public Builder withChunkSize(SignedLong chunkSize) {
            instance.chunkSize = chunkSize;
            return this;
        }

        public Builder withNumMarkers(UnsignedShort numMarkers) {
            instance.numMarkers = numMarkers;
            return this;
        }

        public Builder withMarker(Marker marker) {
            instance.markers.add(marker);
            return this;
        }
    }

    public static MarkerChunk readMarkerChunk(IffReader iffReader) throws IOException {

        SignedLong chunkSize = iffReader.readSignedLong();
        UnsignedShort numMarkers = iffReader.readUnsignedShort();

        final MarkerChunk.Builder markerChunkBuilder = new MarkerChunk.Builder()
                .withChunkSize(chunkSize)
                .withNumMarkers(numMarkers);

        for (int i = 0; i < numMarkers.toInt(); i++) {
            final SignedShort markerId = iffReader.readSignedShort();
            final UnsignedLong position = iffReader.readUnsignedLong();
            final PString markerName = iffReader.readPString();
            markerChunkBuilder.withMarker(new MarkerChunk.Marker(markerId, position, markerName));
        }

        return markerChunkBuilder.build();
    }

    public static class MarkerChunkReader implements ChunkReader {
        @Override
        public Chunk readChunk(IffReader reader) throws IOException {
            return readMarkerChunk(reader);
        }
    }
}
