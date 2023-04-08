package com.georgeneokq.game.question;

import com.badlogic.gdx.audio.Music;

public class Question {

    private String name;
    private QuestionType type;
    private String question;
    private Choice[] choices;
    private int correctChoiceIndex;
    private int weightage;
    private Music voice;

    public Question(String name, QuestionType type, String question, Choice[] choices,
                    int correctChoiceIndex) {
        this(name, type, question, choices, correctChoiceIndex, null);
    }

    public Question(String name, QuestionType type, String question, Choice[] choices,
                    int correctChoiceIndex, Music voice) {
        this(name, type, question, choices, correctChoiceIndex, 1, voice);
    }

    public Question(String name, QuestionType type, String question, Choice[] choices,
                    int correctChoiceIndex, int weightage, Music voice) {
        this.name = name;
        this.type = type;
        this.question = question;
        this.choices = choices;
        this.correctChoiceIndex = correctChoiceIndex;
        this.weightage = weightage;
        this.voice = voice;
    }

    public static QuestionType getTypeByString(String type) {
        switch(type.toLowerCase()) {
            case "listening":
                return QuestionType.LISTENING;
            default:
                return QuestionType.TEXT;
        }
    }

    public String getName() {
        return name;
    }

    public QuestionType getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    private Choice getCorrectChoice() {
        return choices[correctChoiceIndex];
    }

    public int getCorrectChoiceIndex() {
        return correctChoiceIndex;
    }

    public int getWeightage() {
        return weightage;
    }

    public void setWeightage(int weightage) {
        this.weightage = weightage;
    }

    public Choice[] getChoices() {
        return choices;
    }

    public void setChoices(Choice[] choices) {
        this.choices = choices;
    }

    public Music getVoice() {
        return voice;
    }

    public void setVoice(Music voice) {
        this.voice = voice;
    }
}
