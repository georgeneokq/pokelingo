package com.georgeneokq.game.actor.npc;

import com.badlogic.gdx.graphics.Texture;
import com.georgeneokq.game.dialog.Dialog;
import com.georgeneokq.game.question.Question;

public interface Opponent {
    Texture getBattleSprite(); // Opponent image to be drawn in the battle screen
    int getMaxHP();
    Question[] getQuestions();
    Question getNextQuestion();
    String getBattleMusicName(); // Includes file extension
    Dialog getWinDialog();
}
