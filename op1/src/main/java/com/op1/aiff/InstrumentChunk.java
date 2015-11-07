package com.op1.aiff;

import com.op1.iff.Chunk;
import com.op1.iff.IffReader;
import com.op1.iff.types.*;
import com.op1.util.Check;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InstrumentChunk implements Chunk {

    private final ID chunkId = ChunkType.INSTRUMENT.getChunkId();

    private SignedLong chunkSize;
    private SignedChar baseNote;
    private SignedChar detune;
    private SignedChar lowNote;
    private SignedChar highNote;
    private SignedChar lowVelocity;
    private SignedChar highVelocity;
    private SignedShort gain;
    private Loop sustainLoop;
    private Loop releaseLoop;

    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentChunk.class);

    private InstrumentChunk() {
    }

    private InstrumentChunk(InstrumentChunk chunk) {

        this.chunkSize = DataTypes.copyOf(chunk.getChunkSize());
        this.baseNote = DataTypes.copyOf(chunk.getBaseNote());
        this.detune = DataTypes.copyOf(chunk.getDetune());
        this.lowNote = DataTypes.copyOf(chunk.getLowNote());
        this.highNote = DataTypes.copyOf(chunk.getHighNote());
        this.lowVelocity = DataTypes.copyOf(chunk.getLowVelocity());
        this.highVelocity = DataTypes.copyOf(chunk.getHighVelocity());
        this.gain = DataTypes.copyOf(chunk.getGain());

        this.sustainLoop = new Loop(
                chunk.getSustainLoop().getPlayMode(),
                chunk.getSustainLoop().getBeginLoop(),
                chunk.getSustainLoop().getEndLoop());

        this.releaseLoop = new Loop(
                chunk.getReleaseLoop().getPlayMode(),
                chunk.getReleaseLoop().getBeginLoop(),
                chunk.getReleaseLoop().getEndLoop());

        LOGGER.debug(this.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InstrumentChunk that = (InstrumentChunk) o;

        if (chunkId != null ? !chunkId.equals(that.chunkId) : that.chunkId != null) return false;
        if (chunkSize != null ? !chunkSize.equals(that.chunkSize) : that.chunkSize != null) return false;
        if (baseNote != null ? !baseNote.equals(that.baseNote) : that.baseNote != null) return false;
        if (detune != null ? !detune.equals(that.detune) : that.detune != null) return false;
        if (lowNote != null ? !lowNote.equals(that.lowNote) : that.lowNote != null) return false;
        if (highNote != null ? !highNote.equals(that.highNote) : that.highNote != null) return false;
        if (lowVelocity != null ? !lowVelocity.equals(that.lowVelocity) : that.lowVelocity != null) return false;
        if (highVelocity != null ? !highVelocity.equals(that.highVelocity) : that.highVelocity != null) return false;
        if (gain != null ? !gain.equals(that.gain) : that.gain != null) return false;
        if (sustainLoop != null ? !sustainLoop.equals(that.sustainLoop) : that.sustainLoop != null) return false;
        return !(releaseLoop != null ? !releaseLoop.equals(that.releaseLoop) : that.releaseLoop != null);

    }

    @Override
    public int hashCode() {
        int result = chunkId != null ? chunkId.hashCode() : 0;
        result = 31 * result + (chunkSize != null ? chunkSize.hashCode() : 0);
        result = 31 * result + (baseNote != null ? baseNote.hashCode() : 0);
        result = 31 * result + (detune != null ? detune.hashCode() : 0);
        result = 31 * result + (lowNote != null ? lowNote.hashCode() : 0);
        result = 31 * result + (highNote != null ? highNote.hashCode() : 0);
        result = 31 * result + (lowVelocity != null ? lowVelocity.hashCode() : 0);
        result = 31 * result + (highVelocity != null ? highVelocity.hashCode() : 0);
        result = 31 * result + (gain != null ? gain.hashCode() : 0);
        result = 31 * result + (sustainLoop != null ? sustainLoop.hashCode() : 0);
        result = 31 * result + (releaseLoop != null ? releaseLoop.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InstrumentChunk{" +
                "chunkId=" + chunkId +
                ", chunkSize=" + chunkSize +
                ", baseNote=" + baseNote +
                ", detune=" + detune +
                ", lowNote=" + lowNote +
                ", highNote=" + highNote +
                ", lowVelocity=" + lowVelocity +
                ", highVelocity=" + highVelocity +
                ", gain=" + gain +
                ", sustainLoop=" + sustainLoop +
                ", releaseLoop=" + releaseLoop +
                '}';
    }

    @Override
    public ID getChunkID() {
        return chunkId;
    }

    @Override
    public SignedLong getChunkSize() {
        return chunkSize;
    }

    public SignedChar getBaseNote() {
        return baseNote;
    }

    public SignedChar getDetune() {
        return detune;
    }

    public SignedChar getLowNote() {
        return lowNote;
    }

    public SignedChar getHighNote() {
        return highNote;
    }

    public SignedChar getLowVelocity() {
        return lowVelocity;
    }

    public SignedChar getHighVelocity() {
        return highVelocity;
    }

    public SignedShort getGain() {
        return gain;
    }

    public Loop getSustainLoop() {
        return sustainLoop;
    }

    public Loop getReleaseLoop() {
        return releaseLoop;
    }

    public static class Loop {

        private final SignedShort playMode;
        private final SignedShort beginLoop;
        private final SignedShort endLoop;

        public Loop(SignedShort playMode, SignedShort beginLoop, SignedShort endLoop) {
            this.playMode = playMode;
            this.beginLoop = beginLoop;
            this.endLoop = endLoop;
        }

        public SignedShort getPlayMode() {
            return playMode;
        }

        public SignedShort getBeginLoop() {
            return beginLoop;
        }

        public SignedShort getEndLoop() {
            return endLoop;
        }

        public int getSize() {
            return playMode.getSize()
                    + beginLoop.getSize()
                    + endLoop.getSize();
        }

        @Override
        public String toString() {
            return "Loop{" +
                    "playMode=" + playMode +
                    ", beginLoop=" + beginLoop +
                    ", endLoop=" + endLoop +
                    '}';
        }
    }

    @Override
    public int getPhysicalSize() {
        return chunkId.getSize()
                + chunkSize.getSize()
                + baseNote.getSize()
                + detune.getSize()
                + lowNote.getSize()
                + highNote.getSize()
                + lowVelocity.getSize()
                + highVelocity.getSize()
                + gain.getSize()
                + sustainLoop.getSize()
                + releaseLoop.getSize();
    }

    public static class Builder {

        private final InstrumentChunk instance;

        public Builder() {
            this.instance = new InstrumentChunk();
        }

        public InstrumentChunk build() {
            Check.notNull(instance.chunkSize, "Missing chunkSize");
            Check.notNull(instance.baseNote, "Missing baseNote");
            Check.notNull(instance.detune, "Missing detune");
            Check.notNull(instance.lowNote, "Missing lowNote");
            Check.notNull(instance.highNote, "Missing highNote");
            Check.notNull(instance.lowVelocity, "Missing lowVelocity");
            Check.notNull(instance.highVelocity, "Missing highVelocity");
            Check.notNull(instance.gain, "Missing gain");
            Check.notNull(instance.sustainLoop, "Missing sustainLoop");
            Check.notNull(instance.releaseLoop, "Missing releaseLoop");
            return new InstrumentChunk(instance);
        }

        public Builder withChunkSize(SignedLong chunkSize) {
            instance.chunkSize = chunkSize;
            return this;
        }

        public Builder withBaseNote(SignedChar baseNote) {
            instance.baseNote = baseNote;
            return this;
        }

        public Builder withDetune(SignedChar detune) {
            instance.detune = detune;
            return this;
        }

        public Builder withLowNote(SignedChar lowNote) {
            instance.lowNote = lowNote;
            return this;
        }

        public Builder withHighNote(SignedChar highNote) {
            instance.highNote = highNote;
            return this;
        }

        public Builder withLowVelocity(SignedChar lowVelocity) {
            instance.lowVelocity = lowVelocity;
            return this;
        }

        public Builder withHighVelocity(SignedChar highVelocity) {
            instance.highVelocity = highVelocity;
            return this;
        }

        public Builder withGain(SignedShort gain) {
            instance.gain = gain;
            return this;
        }

        public Builder withSustainLoop(Loop sustainLoop) {
            instance.sustainLoop = sustainLoop;
            return this;
        }

        public Builder withReleaseLoop(Loop releaseLoop) {
            instance.releaseLoop = releaseLoop;
            return this;
        }
    }

    public static InstrumentChunk readInstrumentChunk(IffReader reader) throws IOException {
        return new InstrumentChunk.Builder()
                .withChunkSize(reader.readSignedLong())
                .withBaseNote(reader.readSignedChar())
                .withDetune(reader.readSignedChar())
                .withLowNote(reader.readSignedChar())
                .withHighNote(reader.readSignedChar())
                .withLowVelocity(reader.readSignedChar())
                .withHighVelocity(reader.readSignedChar())
                .withGain(reader.readSignedShort())
                .withSustainLoop(readLoop(reader))
                .withReleaseLoop(readLoop(reader))
                .build();
    }

    private static InstrumentChunk.Loop readLoop(IffReader reader) throws IOException {
        final SignedShort playMode = reader.readSignedShort();
        final SignedShort beginLoop = reader.readSignedShort();
        final SignedShort endLoop = reader.readSignedShort();
        return new InstrumentChunk.Loop(playMode, beginLoop, endLoop);
    }

    public static class InstrumentChunkReader implements ChunkReader {
        public Chunk readChunk(IffReader reader) throws IOException {
            return readInstrumentChunk(reader);
        }
    }
}
