package com.cojac.storyteller.domain;

public enum ReadingSpeed {
    HALF(0.5),
    THREE_QUARTERS(0.75),
    ONE(1.0),
    ONE_QUARTER(1.25),
    ONE_HALF(1.5);

    private final double value;

    ReadingSpeed(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
