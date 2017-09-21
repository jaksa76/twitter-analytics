package com.zuhlke.ta.sentiment.pipeline.impl;

import com.zuhlke.ta.sentiment.model.WeightedWord;
import com.zuhlke.ta.sentiment.pipeline.Enhancer;
import com.zuhlke.ta.sentiment.utils.Dictionaries;
import com.zuhlke.ta.sentiment.utils.Dictionary;
import com.zuhlke.ta.sentiment.utils.DictionaryConstans;

import java.io.IOException;
import java.util.List;

import static com.zuhlke.ta.sentiment.utils.POSUtils.isModal;
import static com.zuhlke.ta.sentiment.utils.POSUtils.stripWord;
import static java.lang.Math.max;


public class IrrealisEnhancer implements Enhancer {
    private static final int MAXIMUM_DISTANCE = 7;
    private final Dictionary irrealisWords;

    public IrrealisEnhancer() throws IOException {
        irrealisWords = Dictionaries.singleFileDictionaryFrom(DictionaryConstans.IRREALIS_FILE);
    }

    public List<WeightedWord> enhance(List<WeightedWord> words) {
        for (int i = 0; i < words.size(); i++) {
            WeightedWord word = words.get(i);

            if (word.isOpinionWord()) {
                for (int j = max(0, i - 1); j >= max(0, i - MAXIMUM_DISTANCE); j--) {
                    WeightedWord context = words.get(j);
                    if (isIrrealisWord(context) || isModal(context.getWord())) {
                        word.setOpinionWord(false);
                        break;
                    } else if (isStopMark(context)) {
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

    private boolean isIrrealisWord(WeightedWord word) {
        String ww = stripWord(word.getWord());
        return irrealisWords.contains(ww);
    }
}
