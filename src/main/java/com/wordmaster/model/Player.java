package com.wordmaster.model;

import javax.xml.bind.annotation.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

    void addWord(String newWord) {
        score += newWord.length();
        words.add(newWord);
    }

    void removeLastWord() {
        if (words.size() > 0) {
            String lastWord = words.get(words.size()-1);
            score -= lastWord.length();
            words.remove(words.size()-1);
        }
    }

    void clearWords() {
        words.clear();
        score = 0;
    }

    public String getName() {
        return name;
    }

    public List<String> getWords() {
        return words;
    }

    public String getLastWord() {
        return (words.size() > 0) ? words.get(words.size()-1) : null;
    }

    public int getScore() {
        return score;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Player) {
            Player obj2compare = (Player) obj;
            return obj2compare.name.equals(name);
        } else return false;
    }
    @XmlAttribute
    public boolean isComputer() {
        return false;
    }
}
