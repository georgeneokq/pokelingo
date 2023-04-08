package com.georgeneokq.game.manager;

import com.badlogic.gdx.audio.Music;

public class DialogAudioManager {

    private static DialogAudioManager dialogAudioManager;

    private DialogAudioManager() {}

    public static DialogAudioManager getInstance() {
        if(dialogAudioManager == null)
            dialogAudioManager = new DialogAudioManager();
        return dialogAudioManager;
    }

    private Music audio;

    public void playAudio(Music audio) {
        if(this.audio != null) {
            this.audio.stop();
        }

        // TODO: Account for volume settings
        this.audio = audio;
        if(audio != null)
            audio.play();
    }

    public void resumeAudio() {
        if(audio != null)
            audio.play();
    }

    public void pauseAudio() {
        if(audio != null)
            audio.pause();
    }

    public void stopAudio() {
        if(audio != null)
            audio.stop();
    }
}
