package com.zuhlke.ta.sentiment.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Implements a dictionary of Ngrams.
 * This dictionary separates the word and
 * the score by a tab
 *
 * @author hadoop
 */
public class NGramFileDictionary extends MappingFileDictionary {

    private NGramFileDictionary(Map<String, Float> words) throws IOException {
        super(words);
    }

    public static NGramFileDictionary fromFile(String dictPath) throws IOException {
        return new NGramFileDictionary(readDictionaryFrom(dictPath, line -> StringUtils.split(line, ':')));
    }

}
