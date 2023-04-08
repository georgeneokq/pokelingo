package com.georgeneokq.engine.settings;

public class NumberSetting extends Setting<Float> {

    private float min = 0;
    private float max = 100;
    private float stepSize = 1;

    public NumberSetting() { }

    public NumberSetting(String name, String label, float value) {
        this.name = name;
        this.label = label;
        this.value = value;
    }

    public NumberSetting(String name, String label, float value, float min, float max, float stepSize) {
        super(name, label, value);
        this.min = min;
        this.max = max;
        this.stepSize = stepSize;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getStepSize() {
        return stepSize;
    }

    @Override
    public void setValue(Object value) {
        this.value = Float.valueOf(value.toString());
    }

    @Override
    public String getSerializableValue() {
        return String.valueOf(value);
    }

    @Override
    public void handleExtra(String key, Object value) {
        super.handleExtra(key, value);
        if(key.equals("min")) {
            this.min = Float.parseFloat(value.toString());
        }
        else if(key.equals("max")) {
            this.max = Float.parseFloat(value.toString());
        }
        else if(key.equals("step_size")) {
            this.stepSize = Float.parseFloat(value.toString());
        }
    }
}
