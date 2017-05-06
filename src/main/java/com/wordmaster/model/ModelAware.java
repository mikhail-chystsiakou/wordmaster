package com.wordmaster.model;

import com.wordmaster.model.exception.ModelException;

public interface ModelAware {
    void onMove();
    void onFinish();
    void onInvalidMove();
}
