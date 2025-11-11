package com.blacksnow1002.realmmod.system.broadcast.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColoredText {
    private final List<TextSegment> segments = new ArrayList<>();

    public ColoredText(String text) {
        parseText(text);
    }

    private void parseText(String text) {
        // 支援 § 顏色代碼和 &# 十六進制代碼
        Pattern pattern = Pattern.compile("(§[0-9a-fk-or]|&#[0-9A-Fa-f]{6})");
        Matcher matcher = pattern.matcher(text);

        int currentColor = 0xFFFFFF; // 預設白色
        int lastIndex = 0;

        while (matcher.find()) {
            // 添加當前顏色的文字
            if (matcher.start() > lastIndex) {
                String segment = text.substring(lastIndex, matcher.start());
                if (!segment.isEmpty()) {
                    segments.add(new TextSegment(segment, currentColor));
                }
            }

            // 解析新顏色
            String colorCode = matcher.group();
            if (colorCode.startsWith("&#")) {
                // 十六進制顏色
                currentColor = parseHexColor(colorCode.substring(2));
            } else {
                // Minecraft 顏色代碼
                currentColor = getMinecraftColor(colorCode.charAt(1));
            }

            lastIndex = matcher.end();
        }

        // 添加剩餘文字
        if (lastIndex < text.length()) {
            String segment = text.substring(lastIndex);
            if (!segment.isEmpty()) {
                segments.add(new TextSegment(segment, currentColor));
            }
        }

        // 如果沒有任何片段，添加整個文字作為白色
        if (segments.isEmpty() && !text.isEmpty()) {
            segments.add(new TextSegment(text, 0xFFFFFF));
        }
    }

    private int parseHexColor(String hex) {
        try {
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    private int getMinecraftColor(char code) {
        return switch (code) {
            case '0' -> 0x000000; // 黑色
            case '1' -> 0x0000AA; // 深藍色
            case '2' -> 0x00AA00; // 深綠色
            case '3' -> 0x00AAAA; // 深青色
            case '4' -> 0xAA0000; // 深紅色
            case '5' -> 0xAA00AA; // 深紫色
            case '6' -> 0xFFAA00; // 金色
            case '7' -> 0xAAAAAA; // 灰色
            case '8' -> 0x555555; // 深灰色
            case '9' -> 0x5555FF; // 藍色
            case 'a' -> 0x55FF55; // 綠色
            case 'b' -> 0x55FFFF; // 青色
            case 'c' -> 0xFF5555; // 紅色
            case 'd' -> 0xFF55FF; // 洋紅色
            case 'e' -> 0xFFFF55; // 黃色
            case 'f' -> 0xFFFFFF; // 白色
            default -> 0xFFFFFF;
        };
    }

    public List<TextSegment> getSegments() {
        return segments;
    }

    public static class TextSegment {
        public final String text;
        public final int color;

        public TextSegment(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }
}