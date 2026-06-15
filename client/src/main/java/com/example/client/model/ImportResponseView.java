package com.example.client.model;

/** Aggregate Part 1 import response (xml + json results). */
public class ImportResponseView {

    private FileResultView xml;
    private FileResultView json;
    private Boolean allValid;

    public FileResultView getXml() {
        return xml;
    }

    public void setXml(FileResultView xml) {
        this.xml = xml;
    }

    public FileResultView getJson() {
        return json;
    }

    public void setJson(FileResultView json) {
        this.json = json;
    }

    public Boolean getAllValid() {
        return allValid;
    }

    public void setAllValid(Boolean allValid) {
        this.allValid = allValid;
    }
}
