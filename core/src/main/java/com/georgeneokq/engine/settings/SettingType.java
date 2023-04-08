package com.georgeneokq.engine.settings;

public enum SettingType {
    HOTKEY("hotkey"),
    NUMBER("number"),
    TEXT("text"),
    BOOLEAN("boolean"),
    SELECT("select");
    // Declare new setting types here!

    private final String value;

    SettingType(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
