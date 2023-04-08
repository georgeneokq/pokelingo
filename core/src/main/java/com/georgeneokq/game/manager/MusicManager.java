package com.georgeneokq.game.manager;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

import java.util.HashMap;
import java.util.Map;

public class MusicManager {
    private static MusicManager instance;
    private Music music;
    private String musicName;
    private AssetManager assetManager;
    private float volume = 1f;
    private float previousVolume = volume;

    // Map music name to loop points
    private static Map<String, Float> loopPoints = new HashMap<>();
    static {
        loopPoints.put("audio/bgm/wild_pokemon_battle.wav", 2.43f);
        loopPoints.put("audio/bgm/team_aqua_magma_battle.wav", 101.5f);
        loopPoints.put("audio/bgm/trainer_battle.wav", 34.5f);
        loopPoints.put("audio/bgm/gym_leader_battle.wav", 42f);
    }

    // Private constructor
    private MusicManager() {
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public void initialize(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Music playMusic(String filePath) {
        return playMusic(filePath, true);
    }

    public Music playMusic(String filePath, boolean looping) {
        if(music != null && musicName != null && musicName.equals(filePath))
            return music;

        if (music != null) {
            // Don't allow instance to play 2 bgm at the same time.
            music.dispose();
        }

        music = assetManager.get(filePath, Music.class);
        musicName = filePath;
        music.setLooping(looping);
        music.setVolume(volume);
        music.play();

        if(loopPoints.containsKey(filePath)) {
            setLoopStartPosition(loopPoints.get(filePath));
        }

        return music;
    }

    public void setMusicVolume(float volume) {
        previousVolume = this.volume;
        this.volume = volume;
        if(music != null) {
            music.setVolume(volume);
        }
    }

    public void limitVolume(float volume) {
        if(this.volume > volume) {
            setMusicVolume(volume);
        }
    }

    public void restorePreviousVolume() {
        setMusicVolume(previousVolume);
    }

    public void setLoopStartPosition(float position) {
        if(music == null) return;
        music.setLooping(false);
        music.setOnCompletionListener(music -> {
            music.play();
            music.setPosition(position);
        });
    }

    public void setLooping(boolean looping) {
        if(music == null) return;
        music.setLooping(looping);
    }

    public void resetLoopStartPosition() {
        music.setOnCompletionListener(null);
    }

    public void stopMusic() {
        if (music != null) {
            music.stop();
            music.dispose();
            music = null;
        }
    }

    public Music getMusic() {
        return music;
    }
}
