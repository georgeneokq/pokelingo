package com.georgeneokq.game.widget;

public class MoveSelectOptions {
    private String[] options = new String[4];

    public MoveSelectOptions() {}

    public MoveSelectOptions(String ...options){
        // Only allow 4 options
        for(int i = 0; i < 4; i++) {
            this.options[i] = options[i];
        }
    }

    public String getOption(int index) {
        if(index < 0 || index >= 4)
            return null;
        return options[index];
    }

    public void setOption(int index, String option) {
        if(index >= 0 && index < 4)
            options[index] = option;
    }
}
