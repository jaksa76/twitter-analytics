package com.zuhlke.ta.sentiment.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class Dictionaries {
    public static Dictionary singleFileDictionaryFrom(String dictPath) throws IOException {
        final Map<String, Float> words = DictionaryLineReaderFactory.getInstance().getReader()
                .linesFrom(dictPath)
                .map(StringUtils::split)
                .collect(toMap(toWord, toWeight, overwriteWithLatest));

        return new SingleFileDictionary(words);
    }


    private static final Function<String[], Float> toWeight = splitLine -> splitLine.length > 1 ? Float.parseFloat(splitLine[1]) : 0f;
    private static final Function<String[], String> toWord = splitLine -> splitLine[0];
    private static final BinaryOperator<Float> overwriteWithLatest =  (prev, newer) -> newer;
}
