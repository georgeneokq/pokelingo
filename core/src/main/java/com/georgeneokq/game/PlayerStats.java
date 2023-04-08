package com.georgeneokq.game;

public class PlayerStats {
    private int perfectRuns;
    private int highestCombo;

    private int gymClearCount;

    public PlayerStats() {}

    public PlayerStats(int perfectRuns, int highestCombo, int gymClearCount) {
        this.perfectRuns = perfectRuns;
        this.highestCombo = highestCombo;
        this.gymClearCount = gymClearCount;
    }

    public int getPerfectRuns() {
        return perfectRuns;
    }

    public void setPerfectRuns(int perfectRuns) {
        this.perfectRuns = perfectRuns;
    }

    public int getHighestCombo() {
        return highestCombo;
    }

    public void setHighestCombo(int highestCombo) {
        this.highestCombo = highestCombo;
    }

    public int getGymClearCount() {
        return gymClearCount;
    }

    public void setGymClearCount(int gymClearCount) {
        this.gymClearCount = gymClearCount;
    }
}
