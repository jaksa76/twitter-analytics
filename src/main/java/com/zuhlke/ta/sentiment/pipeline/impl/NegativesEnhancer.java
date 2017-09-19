package com.zuhlke.ta.sentiment.pipeline.impl;

import com.zuhlke.ta.sentiment.model.Negator;
import com.zuhlke.ta.sentiment.model.WeightedWord;
import com.zuhlke.ta.sentiment.pipeline.Enhancer;
import com.zuhlke.ta.sentiment.utils.Dictionaries;
import com.zuhlke.ta.sentiment.utils.Dictionary;
import com.zuhlke.ta.sentiment.utils.DictionaryConstans;

import java.io.IOException;
import java.util.List;

import static com.zuhlke.ta.sentiment.utils.POSUtils.isSubject;
import static com.zuhlke.ta.sentiment.utils.POSUtils.stripWord;
import static java.lang.Math.max;


@SuppressWarnings("WeakerAccess")
public class NegativesEnhancer implements Enhancer {
    private static final int MAXIMUM_DISTANCE = 5;
    private final Dictionary negationWords;

    public NegativesEnhancer(Dictionary negationWords) throws IOException {
        this.negationWords = negationWords;
    }

    public List<WeightedWord> enhance(List<WeightedWord> words) {
        for (int i = 0; i < words.size(); i++) {
            WeightedWord word = words.get(i);

            if (word.isOpinionWord()) {
                for (int j = max(0, i - 1); j >= max(0, i - MAXIMUM_DISTANCE); j--) {
                    WeightedWord previousWord = words.get(j);
                    if (isNegationWord(previousWord)) {
                        word.addNegator(new Negator(previousWord.getWord()));
                    } else if (previousWord.isOpinionWord() || isStopMark(previousWord) || isSubject(previousWord.getWord())) {
                        break;
                    }
                }
            }
        }

        return words;
    }

    private boolean isStopMark(WeightedWord word) {
        return word.isLimitWord();
    }

    private boolean isNegationWord(WeightedWord weightedWord) {
        return negationWords.contains(stripWord(weightedWord.getWord()));
    }

    public static NegativesEnhancer negativesFinder() throws IOException {
        return new NegativesEnhancer(Dictionaries.singleFileDictionaryFrom(DictionaryConstans.NEGATORS_FILE));
    }

}
