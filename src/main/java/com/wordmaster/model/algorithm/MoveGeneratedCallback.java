package com.wordmaster.model.algorithm;

import com.wordmaster.model.Move;

/**
 * Callback to call after move generation.
 *
 * @author Mike
 * @version 1.0
 */
public interface MoveGeneratedCallback {
    void onMove(Move move);
}
