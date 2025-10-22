package com.blacksnow1002.realmmod.broadcast;

import com.blacksnow1002.realmmod.broadcast.util.ColoredText;

public class BroadcastData {
    private final String message;
    private final int duration;
    private final ColoredText coloredText;

    public BroadcastData(String message, int duration) {
        this.message = message;
        this.duration = duration;
        this.coloredText = new ColoredText(message);
    }

    public String getMessage() {
        return message;
    }

    public int getDuration() {
        return duration;
    }

    public ColoredText getColoredText() {
        return coloredText;
    }
}
