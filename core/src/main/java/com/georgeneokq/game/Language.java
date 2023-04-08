package com.georgeneokq.game;

public enum Language {
    EN,
    JP,
    JP_KANA;

    Language() {}

    public static Language fromString(String string) {
        switch(string.toLowerCase()) {
            case "en":
                return EN;
            case "jp":
                return JP;
            case "jp_kana":
                return JP_KANA;
            default:
                return null;
        }
    }
}
