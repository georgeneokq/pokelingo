package com.georgeneokq.game.dialog;

import com.badlogic.gdx.audio.Music;

public class Dialog {
    private Line[] lines;

    public Dialog(Line[] lines) {
        this.lines = lines;
    }

    public Line[] getLines() {
        return lines;
    }

    public void setLine(Line[] lines) {
        this.lines = lines;
    }

    public static class Line {
        private String originalText;
        private String subtitle;
        private Music audio;

        public Line(String originalText, String subtitle, Music audio) {
            this.originalText = originalText;
            this.subtitle = subtitle;
            this.audio = audio;
        }

        public String getOriginalText() {
            return originalText;
        }

        public void setOriginalText(String originalText) {
            this.originalText = originalText;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public Music getAudio() {
            return audio;
        }

        public void setAudio(Music audio) {
            this.audio = audio;
        }
    }
}
