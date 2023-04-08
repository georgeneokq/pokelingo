package com.georgeneokq.engine.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This class emits events to subscribers. Events may optionally carry data.
 */
public class EventManager {

    public interface Subscriber {
        void eventReceived(String eventName, Object data);
    }

    // Map event name to a list of subscribers
    public Map<String, List<Subscriber>> eventsMap = new HashMap<>();

    private static EventManager eventManager;

    public static EventManager getInstance() {
        if(eventManager == null)
            eventManager = new EventManager();
        return eventManager;
    }

    public void subscribe(Subscriber subscriber, String[] events) {
        subscribe(subscriber, Arrays.asList(events));
    }

    public void subscribe(Subscriber subscriber, List<String> events) {
        for(String event : events) {
            eventsMap.computeIfAbsent(event, key -> new ArrayList<>());

            List<Subscriber> eventSubscriberList = eventsMap.get(event);
            eventSubscriberList.add(subscriber);
        }
    }

    // Unsubscribe method for cleanup.
    // TODO: Improve algorithm
    public void unsubscribe(Subscriber subscriber) {
        for(List<Subscriber> eventSubscribersList : eventsMap.values()) {
            eventSubscribersList.removeIf(existingSubscriber -> existingSubscriber == subscriber);
        }
    }

    public void emit(String eventName) {
        emit(eventName, null);
    }

    public void emit(String eventName, Object data) {
        List<Subscriber> subscribers = eventsMap.get(eventName);
        if(subscribers == null)
            return;

        for(Subscriber subscriber : subscribers) {
            subscriber.eventReceived(eventName, data);
        }
    }
}
