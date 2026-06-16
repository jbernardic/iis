package com.example.client.model;

import java.util.ArrayList;
import java.util.List;

public class FileResultView {

    private String format;
    private boolean valid;
    private List<String> errors = new ArrayList<>();
    private Long savedOrderId;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Long getSavedOrderId() {
        return savedOrderId;
    }

    public void setSavedOrderId(Long savedOrderId) {
        this.savedOrderId = savedOrderId;
    }
}
