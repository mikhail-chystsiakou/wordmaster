package com.wordmaster.model;

import javax.xml.bind.annotation.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Represents human player
 *
 * @version 1.0
 * @author Mike
 */
@XmlType
@XmlAccessorType(XmlAccessType.NONE)
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class Player {
    public static final int MAX_NAME_LENGTH = 10;
    public static final int MIN_NAME_LENGTH = 2;

    @XmlAttribute
    protected String name;

    @XmlAttribute
    protected int score;

    @XmlElement
    @XmlList
    protected List<String> words = Collections.synchronizedList(new LinkedList<>());

    // for jaxb
    public Player() {

    }

    public Player(String name) {
        this.name = name;
    }

    /**
     * Adds word to the list of player words and updates it's score
     *
     * @param newWord word to add
     */
    void addWord(String newWord) {
        score += newWord.length();
        words.add(newWord);
    }

    /**
     * Removes last word from the list of player words and updates it's score
     */
    void removeLastWord() {
        if (words.size() > 0) {
            String lastWord = words.get(words.size()-1);
            score -= lastWord.length();
            words.remove(words.size()-1);
        }
    }

    /**
     * Removes all player words and zeroes it's score
     */
    void clearWords() {
        words.clear();
        score = 0;
    }

    /**
     * Getter for player name
     *
     * @return player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for player words
     *
     * @return player's words
     */
    public List<String> getWords() {
        return words;
    }

    /**
     * Getter for player score
     *
     * @return player's score
     */
    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Player) {
            Player obj2compare = (Player) obj;
            return obj2compare.name.equals(name);
        } else return false;
    }

    /**
     * Allows to check if player is computer
     *
     * @return true if player is computer, false otherwise
     */
    @XmlAttribute
    public boolean isComputer() {
        return false;
    }
}
