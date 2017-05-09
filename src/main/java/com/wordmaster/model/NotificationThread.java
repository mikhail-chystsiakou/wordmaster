package com.wordmaster.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

class NotificationThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(NotificationThread.class);
    private Deque<Runnable> notificationTaskQueue = new ConcurrentLinkedDeque<>();
    private boolean killFlag = false;

    public NotificationThread() {
        setName("NotificationThread");
    }

    @Override
    public void run() {
        while(true) {
            try {
                while(notificationTaskQueue.size() == 0 && !killFlag) {
                    synchronized (this) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                logger.error("Notification thread waiting was suddenly interrupted", e);
                return;
            }
            notificationTaskQueue.pollFirst().run();
            if (killFlag && notificationTaskQueue.size() == 0) {
                return;
            }
        }
    }

    public void addTask(Runnable task) {
        notificationTaskQueue.addLast(task);
        synchronized (this) {
            notify();
        }
    }

    public void raiseDeath() {
        synchronized (this) {
            killFlag = true;
            notify();
        }
    }
}
