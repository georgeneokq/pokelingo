package com.georgeneokq.game.event;

public class SelectionResponseEventData {
    private String response;
    private Object requestor;

    public SelectionResponseEventData(String response, Object requestor) {
        this.response = response;
        this.requestor = requestor;
    }

    public String getResponse() {
        return response;
    }

    public Object getRequestor() {
        return requestor;
    }
}
