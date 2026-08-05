package com.georgeneokq.game;

public enum Language {
    EN,
    JP;

    Language() {}

    public static Language fromString(String string) {
        switch(string.toLowerCase()) {
            case "en":
                return EN;
            case "jp":
                return JP;
            default:
                return null;
        }
    }
}
