package com.wordmaster.model;

import com.wordmaster.model.exception.ModelStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class to control model thread operations. Thread has 3 phase:
 * Aimed to control that, first, only one operation
 * is in progress, and, second, to allow ability to freeze model
 * thread in stable state, when no operations can be applied until
 * unfreeze.
 * Note that the order of methods startOperation, applyOperation and
 * endOperation is important.
 *
 * @author Mike
 * @version 1.0
 */
public class ModelScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ModelScheduler.class);

    private GameModel model;

    private Thread modelThread;
    private Semaphore operationInProgress = new Semaphore(1);   // 0 if in progress
    private boolean operationInProgressFlag = false;
    private final Object operationApplicationMonitor = new Object();  // 0 during application
    private int freezeCount;
    private boolean threadDeathFlag = false;

    ModelScheduler(GameModel model) {
        this.model = model;
    }

    /**
     * Starts the model thread
     */
    void runModelThread() {
        modelThread = new Thread(() -> {
            model.modelThread();
        });
        modelThread.setName("modelThread");
        modelThread.start();
    }

    /**
     * Notifies the model thread that new operation is coming and checks
     * if there is another operation in progress.
     */
    synchronized void startOperation() {
        if (!operationInProgress.tryAcquire()) {
            logger.trace("Cannot make move cause another move is in progress");
            throw new ModelStateException("Another move in progress", null);
        }
        operationInProgressFlag = true;
        notify();
    }

    /**
     * Tries to switch model in 'operation allying' state. If model
     * has been frozen, blocks until unfreeze.
     */
    void applyOperation() {
        synchronized (operationApplicationMonitor) {
            while (freezeCount > 0) {
                try {
                    operationApplicationMonitor.wait();
                } catch (InterruptedException e) {
                    logger.error("Model thread interrupted during waiting for moves");
                }
            }
        }
    }

    /**
     * Allows another operation to be started.
     */
    synchronized void endOperation() {
        operationInProgressFlag = false;
        operationInProgress.release();
    }

    /**
     * Blocks thread until any model operation comes.
     */
    synchronized void waitForMove() {
        while(!operationInProgressFlag) {
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Model thread interrupted during waiting for moves");
            }
        }
    }

    /**
     * Freezes the model thread. After it until calling unfreeze,
     * no changes can be applied to model.
     */
    void freeze() {
        synchronized (operationApplicationMonitor) {
            freezeCount++;
        }
    }

    /**
     * Unfreezes model that any changes can be applied.
     */
    void unfreeze() {
        synchronized (operationApplicationMonitor) {
            freezeCount--;
            operationApplicationMonitor.notify();
        }
    }

    /**
     * Rises model thread death flag. It will be killed after
     * finishing the last operation.
     */
    synchronized void raiseDeath() {
        threadDeathFlag = true;
        operationInProgressFlag = true;
        notify();
    }

    /**
     * Checks if model thread is death.
     *
     * @return true if model thread is death, false otherwise
     */
    synchronized boolean checkDeath() {
        return threadDeathFlag;
    }
}
