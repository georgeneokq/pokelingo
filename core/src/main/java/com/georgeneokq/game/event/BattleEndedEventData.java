package com.georgeneokq.game.event;

public class BattleEndedEventData {
    private boolean perfectRun;
    private int totalCorrect;
    private int totalWrong;
    private int highestCombo;
    private boolean playerWon;

    public BattleEndedEventData() {}

    public BattleEndedEventData(boolean playerWon, boolean perfectRun, int totalCorrect, int totalWrong,
                                int highestCombo) {
        this.playerWon = playerWon;
        this.perfectRun = perfectRun;
        this.totalCorrect = totalCorrect;
        this.totalWrong = totalWrong;
        this.highestCombo = highestCombo;
    }

    public boolean playerWon() {
        return playerWon;
    }

    public void setPlayerWon(boolean playerWon) {
        this.playerWon = playerWon;
    }

    public boolean isPerfectRun() {
        return perfectRun;
    }

    public void setPerfectRun(boolean perfectRun) {
        this.perfectRun = perfectRun;
    }

    public int getTotalCorrect() {
        return totalCorrect;
    }

    public void setTotalCorrect(int totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public int getTotalWrong() {
        return totalWrong;
    }

    public void setTotalWrong(int totalWrong) {
        this.totalWrong = totalWrong;
    }

    public int getHighestCombo() {
        return highestCombo;
    }

    public void setHighestCombo(int highestCombo) {
        this.highestCombo = highestCombo;
    }
}
