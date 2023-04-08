package com.georgeneokq.engine.animation;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

/**
 * Structured similarly to Transitions class but this one focuses
 * on animating actors, and multiple can be running at once.
 */
public class Animations {
    private Map<Actor, AnimationRenderer> runningAnimations;

    public Animations() {
        this.runningAnimations = new HashMap<>();
    }

    public void startAnimation(Actor actor, AnimationRenderer animationRenderer) {
        if(runningAnimations.containsKey(actor)) {
            AnimationRenderer runningRenderer = runningAnimations.get(actor);
            runningRenderer.stop();
        }
        runningAnimations.put(actor, animationRenderer);
        animationRenderer.start();
    }

    public void render(float delta) {
        if(runningAnimations.size() == 0)
            return;

        // Call render for every animation renderers
        Array<Actor> toRemove = new Array<>();
        for(Map.Entry<Actor, AnimationRenderer> mapEntry : runningAnimations.entrySet()) {
            AnimationRenderer animationRenderer = mapEntry.getValue();
            if(!animationRenderer.isRunning()) {
                toRemove.add(mapEntry.getKey());
            }
            animationRenderer.render(delta);
        }

        // Remove inactive animation instances
        for(Actor actor : toRemove) {
            runningAnimations.remove(actor);
        }
    }
}
