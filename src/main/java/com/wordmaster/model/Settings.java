package com.wordmaster.model;

public class Settings {
    private Settings instance;

    private Settings() {

    }
    public Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
}
