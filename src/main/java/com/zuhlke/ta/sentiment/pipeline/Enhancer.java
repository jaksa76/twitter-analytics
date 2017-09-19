package com.zuhlke.ta.sentiment.pipeline;

import com.zuhlke.ta.sentiment.model.WeightedWord;

import java.util.List;

/**
 * Enhance a detail within each WeightedWord
 */
public interface Enhancer {
    List<WeightedWord> enhance(List<WeightedWord> input);
}
