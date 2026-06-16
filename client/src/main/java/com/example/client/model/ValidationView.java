package com.example.client.model;

import java.util.ArrayList;
import java.util.List;

public class ValidationView {

    private boolean valid;
    private List<String> messages = new ArrayList<>();

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
