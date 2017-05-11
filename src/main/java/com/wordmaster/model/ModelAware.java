package com.wordmaster.model;

/**
 * Interface for all classes, that wants to be notified
 * on any model state change. All notifications will be
 * executed in single separated thread.
 */
public interface ModelAware {
    /**
     * Will be called after each model move.
     *
     * @param model changed model object
     */
    void onMove(GameModel model);

    /**
     * Will be called after game will be finished.
     *
     * @param model changed model object
     */
    void onFinish(GameModel model);

    /**
     * Will be called after invalid move.
     *
     * @param model changed model object
     * @param type invalid move type, see <code>Move</code> constants
     */
    void onInvalidMove(GameModel model, int type);
}
