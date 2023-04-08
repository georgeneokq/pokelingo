package com.georgeneokq.game.event;

import com.georgeneokq.game.dialog.Dialog;

public class SelectionRequestEventData {
    private Dialog dialog;
    private Object requestor;

    public SelectionRequestEventData(Dialog dialog, Object requestor) {
        this.dialog = dialog;
        this.requestor = requestor;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public Object getRequestor() {
        return requestor;
    }
}
