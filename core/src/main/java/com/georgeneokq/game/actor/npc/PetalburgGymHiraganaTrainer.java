package com.georgeneokq.game.actor.npc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.georgeneokq.engine.actor.Actor;
import com.georgeneokq.game.dialog.Dialog;
import com.georgeneokq.game.Globals;
import com.georgeneokq.game.event.DialogEventData;
import com.georgeneokq.game.event.Events;
import com.georgeneokq.game.question.Question;
import com.georgeneokq.game.question.QuestionFilter;
import com.georgeneokq.engine.manager.EventManager;
import com.georgeneokq.engine.manager.ScreensManager;

public class PetalburgGymHiraganaTrainer extends NPC implements Opponent, EventManager.Subscriber {

    private Globals globals;
    private EventManager eventManager;
    private ScreensManager screensManager;
    private Texture battleSprite;
    private Question[] questions;
    private int currentQuestionIndex = -1;

    private String[] subscribedEvents = new String[] {
            Events.DIALOG_ENDED.name()
    };

    public PetalburgGymHiraganaTrainer(float width, float height, TextureAtlas textureAtlas, Texture battleSprite) {
        super(new Rectangle(0, 0, width, height), "Petalburg trainer Kurumi", textureAtlas);

        this.battleSprite = battleSprite;

        globals = Globals.getInstance();
        eventManager = EventManager.getInstance();
        screensManager = ScreensManager.getInstance();
        eventManager.subscribe(this, subscribedEvents);
    }

    private void loadAndShuffleQuestions() {
        QuestionFilter[] questionFilters = new QuestionFilter[] {
                new QuestionFilter((question) -> question.getName().startsWith("hiragana_"), 0),
        };
        questions = globals.getQuestions(questionFilters);
    }

    @Override
    public void actorEnterView(Actor actor) {
        super.actorEnterView(actor);
    }

    @Override
    public void actorInView(Actor actor) {
    }

    @Override
    public void actorLeaveView(Actor actor) {
        super.actorLeaveView(actor);
    }

    @Override
    public void interact(Actor actor) {
        interacting = true;
        Globals globals = Globals.getInstance();
        DialogEventData data = new DialogEventData(
                globals.getDialog("gym_leader_encounter"), this, true);
        direction = actor.getOppositeDirection();
        loadAndShuffleQuestions();
        eventManager.emit(Events.DIALOG_STARTED.name(), data);
    }

    @Override
    public void eventReceived(String eventName, Object data) {
        if(eventName.equals(Events.DIALOG_ENDED.name())) {
            interacting = false;
        }
    }

    @Override
    public Texture getBattleSprite() {
        return battleSprite;
    }

    @Override
    public String getBattleMusicName() {
        return "wild_pokemon_battle.wav";
    }

    @Override
    public int getMaxHP() {
        return 100;
    }

    @Override
    public Question[] getQuestions() {
        return questions;
    }

    @Override
    public Question getNextQuestion() {
        if(currentQuestionIndex + 1 == questions.length) {
            currentQuestionIndex = 0;
        } else {
            currentQuestionIndex += 1;
        }
        return questions[currentQuestionIndex];
    }

    @Override
    public Dialog getWinDialog() {
        return globals.getDialog("gym_leader_win");
    }
}
