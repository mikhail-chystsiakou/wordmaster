package com.wordmaster;

import com.wordmaster.model.algorithm.PrefixTree;
import com.wordmaster.model.algorithm.ReversedPrefixTree;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TreeRunner {
    public static void main(String[] args) throws Exception {
        PrefixTree tree = new PrefixTree();
        InputStream is = TreeRunner.class.getClassLoader()
                .getResourceAsStream("i18n/vocabulary_ru.txt");
        FileInputStream fs = new FileInputStream("voc.txt");
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        String nextWord = bf.readLine();
        int i = 0;
        long t = System.currentTimeMillis();
        while(nextWord != null) {
            tree.addWord(nextWord);
            i++;
            LoggerFactory.getLogger(TreeRunner.class).debug("element {}: {}", i, nextWord);
            nextWord = bf.readLine();
        }
        System.out.println(System.currentTimeMillis() - t);
        //PrefixTree tree2 = tree.goTo("кал");
        System.out.println();
    }
}
