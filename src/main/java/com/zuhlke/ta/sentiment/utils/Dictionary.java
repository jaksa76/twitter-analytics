package com.zuhlke.ta.sentiment.utils;

import java.util.Optional;

/**
 * Implementation of a dictionary.
 * Each word contains a score used by
 * the calculator to sum up the sentiment
 * 
 * @author hadoop
 *
 */
public interface Dictionary {
    float NO_SCORE = 0f;

    Optional<Float> getWordWeight(String word);
    int getWordCount();
    boolean contains(String word);

}
