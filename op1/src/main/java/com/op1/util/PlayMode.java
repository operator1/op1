package com.op1.util;

public enum PlayMode {
    HOLD(6144),
    PLAY(12288),
    LOOP(20480);

    private final int metaDataValue;

    PlayMode(int metaDataValue) {
        this.metaDataValue = metaDataValue;
    }

    public int getMetaDataValue() {
        return metaDataValue;
    }
}
