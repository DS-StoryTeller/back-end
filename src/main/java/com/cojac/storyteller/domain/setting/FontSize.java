package com.cojac.storyteller.domain.setting;

public enum FontSize {
    SMALL("작게"), MEDIUM("보통"), LARGE("크게");

    private final String description;

    FontSize(String description) {
        this.description = description;
    }

}

