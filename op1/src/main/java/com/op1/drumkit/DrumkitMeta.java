package com.op1.drumkit;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.op1.util.Check;

import java.util.Arrays;

public class DrumkitMeta {

    private int drumVersion;
    private String type;
    private String name;
    private int octave;
    private int[] pitch;
    private int[] start;
    private int[] end;
    private int[] playmode;
    private int[] reverse;
    private int[] volume;
    private int[] dynaEnv;
    private boolean fxActive;
    private String fxType;
    private int[] fxParams;
    private boolean lfoActive;
    private String lfoType;
    private int[] lfoParams;

    private DrumkitMeta() {
    }

    public DrumkitMeta(DrumkitMeta meta) {
        this.drumVersion = meta.drumVersion;
        this.type = meta.type;
        this.name = meta.name;
        this.octave = meta.octave;
        this.pitch = Arrays.copyOf(meta.pitch, meta.pitch.length);
        this.start = Arrays.copyOf(meta.start, meta.start.length);
        this.end = Arrays.copyOf(meta.end, meta.end.length);
        this.playmode = Arrays.copyOf(meta.playmode, meta.playmode.length);
        this.reverse = Arrays.copyOf(meta.reverse, meta.reverse.length);
        this.volume = Arrays.copyOf(meta.volume, meta.volume.length);
        this.dynaEnv = Arrays.copyOf(meta.dynaEnv, meta.dynaEnv.length);
        this.fxActive = meta.fxActive;
        this.fxType = meta.fxType;
        this.fxParams = Arrays.copyOf(meta.fxParams, meta.fxParams.length);
        this.lfoActive = meta.lfoActive;
        this.lfoType = meta.lfoType;
        this.lfoParams = Arrays.copyOf(meta.lfoParams, meta.lfoParams.length);
    }

    public int getDrumVersion() {
        return drumVersion;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getOctave() {
        return octave;
    }

    public int[] getPitch() {
        return pitch;
    }

    public int[] getStart() {
        return start;
    }

    public int[] getEnd() {
        return end;
    }

    public int[] getPlaymode() {
        return playmode;
    }

    public int[] getReverse() {
        return reverse;
    }

    public int[] getVolume() {
        return volume;
    }

    public int[] getDynaEnv() {
        return dynaEnv;
    }

    public boolean isFxActive() {
        return fxActive;
    }

    public String getFxType() {
        return fxType;
    }

    public int[] getFxParams() {
        return fxParams;
    }

    public boolean isLfoActive() {
        return lfoActive;
    }

    public String getLfoType() {
        return lfoType;
    }

    public int[] getLfoParams() {
        return lfoParams;
    }

    public static class Builder {

        private final DrumkitMeta instance;

        public Builder() {
            instance = new DrumkitMeta();
        }

        public DrumkitMeta build() {
            Check.state(is24ElementArray(instance.pitch), "Pitch is not a 24 element array");
            Check.state(is24ElementArray(instance.start), "Start is not a 24 element array");
            Check.state(is24ElementArray(instance.end), "End is not a 24 element array");
            Check.state(is24ElementArray(instance.playmode), "Playmode is not a 24 element array");
            Check.state(is24ElementArray(instance.reverse), "Reverse is not a 24 element array");
            Check.state(is24ElementArray(instance.volume), "Volumne is not a 24 element array");
            Check.state(is8ElementArray(instance.dynaEnv), "DynaEnv is not an 8 element array");
            Check.state(is8ElementArray(instance.fxParams), "FxParams is not an 8 element array");
            Check.state(is8ElementArray(instance.lfoParams), "LfoParams is not an 8 element array");
            return new DrumkitMeta(instance);
        }

        public Builder withDrumVersion(int drumVersion) {
            instance.drumVersion = drumVersion;
            return this;
        }

        public Builder withType(String type) {
            instance.type = type;
            return this;
        }

        public Builder withName(String name) {
            instance.name = name;
            return this;
        }

        public Builder withOctave(int octave) {
            instance.octave = octave;
            return this;
        }

        public Builder withPitch(int[] pitch) {
            instance.pitch = pitch;
            return this;
        }

        public Builder withStart(int[] start) {
            instance.start = start;
            return this;
        }

        public Builder withEnd(int[] end) {
            instance.end = end;
            return this;
        }

        public Builder withPlaymode(int[] playmode) {
            instance.playmode = playmode;
            return this;
        }

        public Builder withReverse(int[] reverse) {
            instance.reverse = reverse;
            return this;
        }

        public Builder withVolume(int[] volume) {
            instance.volume = volume;
            return this;
        }

        public Builder withDynaEnv(int[] dynaEnv) {
            instance.dynaEnv = dynaEnv;
            return this;
        }

        public Builder withFxActive(boolean fxActive) {
            instance.fxActive = fxActive;
            return this;
        }

        public Builder withFxType(String fxType) {
            instance.fxType = fxType;
            return this;
        }

        public Builder withFxParams(int[] fxParams) {
            instance.fxParams = fxParams;
            return this;
        }

        public Builder withLfoActive(boolean lfoActive) {
            instance.lfoActive = lfoActive;
            return this;
        }

        public Builder withLfoType(String lfoType) {
            instance.lfoType = lfoType;
            return this;
        }

        public Builder withLfoParams(int[] lfoParams) {
            instance.lfoParams = lfoParams;
            return this;
        }

        private boolean is24ElementArray(int[] array) {
            return array != null && array.length == 24;
        }

        private boolean is8ElementArray(int[] array) {
            return array != null && array.length == 8;
        }
    }

    public static DrumkitMeta fromJson(String jsonString) {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
                .fromJson(jsonString, DrumkitMeta.class);
    }
}
