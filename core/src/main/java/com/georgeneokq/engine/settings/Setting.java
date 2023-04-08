package com.georgeneokq.engine.settings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
 * Classes which inherit from Setting class require a default constructor.
 * This is due to how classes are instantiated in SettingsParser.
 */
public abstract class Setting<T> implements Cloneable {
    protected String name;
    protected String label;
    protected Map<String, Object> extras = new HashMap<>();
    protected T value;

    public Setting() {}

    public Setting(String name, String label, T value) {
        this.name = name;
        this.label = label;
        this.value = value;
    }

    @Override
    public Setting<T> clone() {
        Setting<T> clonedSetting = null;

        try {
            clonedSetting = (Setting<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        clonedSetting.name = name;
        clonedSetting.label = label;
        clonedSetting.value = value;
        clonedSetting.extras = extras;

        return clonedSetting;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public Object getExtra(String key) {
        return extras.getOrDefault(key, null);
    }

    // This will most likely be overridden by subclasses,
    // as every setting type have extra data customized for
    // themselves.
    public void handleExtra(String key, Object value) {
        extras.put(key, value);
    }

    public T getValue() {
        return value;
    }

    // To retrieve the value for serialization, i.e. for saving settings
    public abstract Serializable getSerializableValue();

    // General object parameter for deserialization
    public abstract void setValue(Object value);
}
