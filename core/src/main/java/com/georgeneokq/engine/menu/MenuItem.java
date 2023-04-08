package com.georgeneokq.engine.menu;

import com.badlogic.gdx.audio.Sound;
import com.georgeneokq.engine.manager.SettingsManager;
import com.georgeneokq.engine.settings.NumberSetting;

public class MenuItem {
    public interface OnSelectListener {
        void onSelect();
    }

    private String title;
    private OnSelectListener selectListener;
    private Sound sound;

    public MenuItem(String title, OnSelectListener selectListener, Sound sound) {
       this.title = title;
       this.selectListener = selectListener;
       this.sound = sound;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public OnSelectListener getSelectListener() {
        return selectListener;
    }

    public void setSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public void playSound() {
        float volume = SettingsManager.getInstance().getSetting("general.sfx_volume", NumberSetting.class).getValue();
        if(sound != null)
            sound.play(volume);
    }

}
