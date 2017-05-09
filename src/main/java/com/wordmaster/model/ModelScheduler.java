package com.wordmaster.model;

import com.wordmaster.model.exception.ModelStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ModelScheduler.class);

    private GameModel model;

    private Thread modelThread;
    // Логика работы: если модель переведена с состояние suspend, то никакие
    // изменения не произойдут до resume. Это значит, что возможно продолжение
    // вычислений в потоке, однако не обновление модели
    private int suspendSemaphore = 0;
    private boolean threadDeathFlag = false;
    private boolean moveInProgressFlag = false;

    ModelScheduler(GameModel model) {
        this.model = model;
    }

    void runModelThread() {
        modelThread = new Thread(() -> {
            model.modelThread();
        });
        modelThread.setName("modelThread");
        modelThread.start();
    }
    synchronized void checkOperationInProgressAndLock() throws ModelStateException {
        if (threadDeathFlag) return;
        if (moveInProgressFlag) {
            logger.trace("Cannot make move cause another move is in progress");
            throw new ModelStateException("Another move in progress", null);
        } else {
            moveInProgressFlag = true;
        }
    }
    synchronized void startOperation() throws ModelStateException {
        notify();
    }
    synchronized void endOperation() {
        moveInProgressFlag = false;
    }

    synchronized void waitForMove() {
        while(!moveInProgressFlag) {
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Interrupted during waiting for move", e);
            }
        }
    }

    synchronized void raiseSleep() {
        suspendSemaphore--;
    }
    synchronized void awake() {
        suspendSemaphore++;
        notify();
    }
    synchronized void checkAndSleep() {
        while(suspendSemaphore < 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Interrupted during sleep", e);
            }
        }
    }

    synchronized void raiseDeath() {
        threadDeathFlag = true;
        // if no move in progress
        moveInProgressFlag = true;
        notify();
    }

    synchronized boolean checkDeath() {
        return threadDeathFlag;
    }
}
