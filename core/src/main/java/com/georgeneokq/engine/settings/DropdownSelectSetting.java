package com.georgeneokq.engine.settings;

/*
 * Similar to TextSetting, but contains extra data "choices"
 * which will be split by newlines to render dropdown options.
 */
public class DropdownSelectSetting extends Setting<String> {

    private String[] choices;

    public DropdownSelectSetting() {}

    public DropdownSelectSetting(String name, String label, String value, String[] choices) {
        this.name = name;
        this.label = label;
        this.value = value;
        this.choices = choices;
        this.extras.put("choices", String.join("\n", choices));
    }

    public String[] getChoices() {
        return choices;
    }

    @Override
    public void setValue(Object value) {
        this.value = (String) value;
    }

    @Override
    public void handleExtra(String key, Object value) {
        super.handleExtra(key, value);
        if(key.equals("choices")) {
            // Break at newline
            String valueString = value.toString();
            this.choices = valueString.split("\n");
        }
    }

    @Override
    public String getSerializableValue() {
        return value;
    }
}
