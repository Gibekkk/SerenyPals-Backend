package com.serenypals.restfulapi.enums;

public enum Emoji{
    HAPPY("😊"),
    SAD("😢");
    
    private final String emoji;

    Emoji(String emoji) {
        this.emoji = emoji;
    }

    public String toString() {
        return emoji;
    }

    public static boolean isAvailable(String emoji) {
        for (Emoji s : Emoji.values()) {
            if (s.emoji.equalsIgnoreCase(emoji)) {
                return true;
            }
        }
        return false;
    }

    public static Emoji fromString(String emoji) {
        for (Emoji s : Emoji.values()) {
            if (s.emoji.equalsIgnoreCase(emoji)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Emoji Tidak Diketahui: " + emoji);
    }
}