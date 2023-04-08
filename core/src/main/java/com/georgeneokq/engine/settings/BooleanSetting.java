package com.georgeneokq.engine.settings;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting() {}

    @Override
    public void setValue(Object value) {
        this.value = Boolean.valueOf(value.toString());
    }

    @Override
    public String getSerializableValue() {
        return String.valueOf(value);
    }
}
