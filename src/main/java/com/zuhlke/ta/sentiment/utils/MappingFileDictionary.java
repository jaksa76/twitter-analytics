package com.zuhlke.ta.sentiment.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class MappingFileDictionary implements Dictionary {
    private final Map<String, Float> words;

    private MappingFileDictionary(Map<String, Float> words) {
        this.words = words;
    }

    @Override
    public Optional<Float> getWordWeight(String word) {
        return Optional.ofNullable(words.get(word));
    }

    @Override
    public int getWordCount() {
        return words.size();
    }

    @Override
    public boolean contains(String word) {
        return words.containsKey(word);
    }

    /**
     * Reads a dictionary. The dictionary can be present
     * in the local file system or accesible in other
     * remote accesible location for cluster execution
     *
     * @author hadoop
     */
    public static Dictionary fromSingleFile(String dictPath) throws IOException {
        return new MappingFileDictionary(readDictionaryFrom(dictPath, StringUtils::split));
    }

    /**
     * Implements a dictionary of Ngrams.
     * This dictionary separates the word and
     * the score by a tab
     *
     * @author hadoop
     */
    public static Dictionary fromNgramsFile(String dictPath) throws IOException {
        return new MappingFileDictionary(readDictionaryFrom(dictPath, line -> StringUtils.split(line, ':')));
    }

    private static Map<String, Float> readDictionaryFrom(String fileName, Function<String, String[]> lineSplitter) throws IOException {
        final DictionaryLineReader reader = DictionaryLineReaderFactory.getInstance().getReader();
        return reader.readLines(fileName)
                .map(lineSplitter)
                .collect(toMap(fields -> fields[0], MappingFileDictionary::score));
    }

    private static float score(String[] fields) {
        return fields.length > 1 ? Float.parseFloat(fields[1]) : NO_SCORE;
    }
}
