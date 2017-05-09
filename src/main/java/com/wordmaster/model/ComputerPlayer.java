package com.wordmaster.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;
import java.util.*;

@XmlType
@XmlAccessorType(XmlAccessType.NONE)
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class ComputerPlayer extends Player implements ModelAware {
    private static final Logger logger = LoggerFactory.getLogger(ComputerPlayer.class);
    public enum Difficulty {
        EASY {
            protected TreeMap<Integer, Integer> getTakeProbabilityMap() {
                TreeMap<Integer, Integer> skipProbabilityMap = new TreeMap<>();
                skipProbabilityMap.put(2, 80);
                skipProbabilityMap.put(4, -1);
//                skipProbabilityMap.put(5, 3);
                return skipProbabilityMap;
            }
        },
        MEDIUM {
            protected TreeMap<Integer, Integer> getTakeProbabilityMap() {
                TreeMap<Integer, Integer> skipProbabilityMap = new TreeMap<>();
                skipProbabilityMap.put(2, 30);
                skipProbabilityMap.put(3, 40);
                skipProbabilityMap.put(4, 70);
                skipProbabilityMap.put(5, 70);
                skipProbabilityMap.put(6, 40);
                skipProbabilityMap.put(7, 30);
                return skipProbabilityMap;
            }
        },
        HARD {
            protected TreeMap<Integer, Integer> getTakeProbabilityMap() {
                TreeMap<Integer, Integer> skipProbabilityMap = new TreeMap<>();
                skipProbabilityMap.put(2, -1);
                skipProbabilityMap.put(4, 10);
                skipProbabilityMap.put(5, 100);
//                skipProbabilityMap.put(6, 90);
                return skipProbabilityMap;
            }
        };
        protected abstract TreeMap<Integer, Integer> getTakeProbabilityMap();

        public boolean needToShouldTake(int wordLength) {
            if (wordLength < 2) return true;
            int randomPercent = new Random().nextInt(100);
            TreeMap<Integer, Integer> m = getTakeProbabilityMap();
            return randomPercent < m.floorEntry(wordLength).getValue();
        }
    }
    @XmlAttribute
    private Difficulty difficulty;
    @XmlAttribute
    private int delay;

    public ComputerPlayer() {

    }

    public ComputerPlayer(String name, Difficulty difficulty, int delay) {
        super(name);
        this.difficulty = difficulty;
        this.delay = delay;
    }

    public boolean isComputer() {
        return true;
    }

    Move selectMove(List<Move> variants) {
        if (variants.size() == 0) return null;
        Collections.shuffle(variants);
        Move selectedMove = variants.get(0);
        for (Move m : variants) {
            if (difficulty.needToShouldTake(m.getResultWordSize())) {
                selectedMove = m;
                break;
            }
        }
        return selectedMove;
    }

    @Override
    public void onFinish(GameModel model) {

    }
    @Override
    public void onMove(GameModel model) {
        if (model.isReplay()) return;
        if (model.getCurrentPlayer().equals(this)) {
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    logger.error("Unexpected interrupted exception during computer delay", e);
                    model.generateMove();
                    return;
                }
            }
            model.generateMove();
        }
    }
    @Override
    public void onInvalidMove(GameModel model, int type) {
        // nothing to do, computer doesn't care
    }
}
