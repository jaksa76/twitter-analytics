package com.zuhlke.ta.sentiment.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Reads a dictionary. The dictionary can be present
 * in the local file system or accesible in other
 * remote accesible location for cluster execution
 *
 * @author hadoop
 */
public class SingleFileDictionary extends MappingFileDictionary {

    private SingleFileDictionary(Map<String, Float> words) throws IOException {
        super(words);
    }

    public static SingleFileDictionary fromFile(String dictPath) throws IOException {
        return new SingleFileDictionary(readDictionaryFrom(dictPath, StringUtils::split));
    }
}
