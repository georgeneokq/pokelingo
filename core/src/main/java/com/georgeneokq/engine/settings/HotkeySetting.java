package com.georgeneokq.engine.settings;

import com.badlogic.gdx.Input;

public class HotkeySetting extends Setting<Integer> {
    public HotkeySetting() {}

    public HotkeySetting(String name, String label, Integer value) {
        this.name = name;
        this.label = label;
        this.value = value;
    }

    /*
     * Get the string representation of the hotkey
     */
    public String getValueString() {
        return Input.Keys.toString(value);
    }

    @Override
    public void setValue(Object value) {
        this.value = Input.Keys.valueOf(value.toString());
    }

    @Override
    public String getSerializableValue() {
        return getValueString();
    }
}
