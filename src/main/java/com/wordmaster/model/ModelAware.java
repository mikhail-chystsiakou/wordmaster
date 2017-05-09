package com.wordmaster.model;

public interface ModelAware {
    void onMove(GameModel model);
    void onFinish(GameModel model);
    void onInvalidMove(GameModel model, int type);
}
