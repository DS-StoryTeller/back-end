package com.cojac.storyteller.setting.entity.enums;

public enum ReadingSpeed {
    SLOW(0.5),
    SLIGHTLY_SLOW(0.75),
    NORMAL(1.0),
    SLIGHTLY_FAST(1.25),
    FAST(1.5);

    private final double value;

    ReadingSpeed(double value) {
        this.value = value;
    }
}
