package com.georgeneokq.engine.settings;

public class TextSetting extends Setting<String> {

    private int maxLength = 16;
    private String disallowedChars;

    public TextSetting() { }

    public TextSetting(String name, String label, String value) {
        this.name = name;
        this.label = label;
        this.value = value;
    }

    @Override
    public void setValue(Object value) {
        this.value = String.valueOf(value);
    }

    public String getDisallowedChars() {
        return disallowedChars;
    }

    @Override
    public String getSerializableValue() {
        return value;
    }

    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public void handleExtra(String key, Object value) {
        super.handleExtra(key, value);

        // Use else if so no need to run all statements for every extra value all the time
        if(key.equals("max_length")) {
            this.maxLength = Integer.parseInt(value.toString());
        }
        else if (key.equals("disallowed_characters")) {
            this.disallowedChars = value.toString();
        }
    }
    
    public boolean isCharDisallowed(char c) {
        return this.disallowedChars.contains(String.valueOf(c));
    }
}