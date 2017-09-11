package com.zuhlke.ta.sentiment.utils;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class MappingFileDictionary implements Dictionary {
    private final Map<String, Float> words;

    MappingFileDictionary(Map<String, Float> words) {
        this.words = words;
    }

    @Override
    public float getWordWeight(String word) throws TokenNotFound {
        if (words.containsKey(word))
            return words.get(word);
        else
            throw new TokenNotFound("Word not found " + word);
    }

    @Override
    public int getWordCount() {
        return words.size();
    }

    @Override
    public boolean contains(String word) {
        return words.containsKey(word);
    }

    static Map<String, Float> readDictionaryFrom(String fileName, Function<String, String[]> lineSplitter) throws IOException {
        final DictionaryLineReader reader = DictionaryLineReaderFactory.getInstance().getReader();
        return reader.readLines(fileName)
                .map(lineSplitter)
                .collect(toMap(fields -> fields[0], MappingFileDictionary::score));
    }

    private static float score(String[] fields) {
        return fields.length > 1 ? Float.parseFloat(fields[1]) : NO_SCORE;
    }
}
