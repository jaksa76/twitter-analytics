package com.zuhlke.ta.sentiment.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/** 
 * Reads a dictionary. The dictionary can be present
 * in the local file system or accesible in other
 * remote accesible location for cluster execution
 * 
 * @author hadoop
 *
 */
public class SingleFileDictionary implements Dictionary {
	private final Map<String, Float> words;

	private SingleFileDictionary(Map<String, Float> words) {
		this.words = words;
	}

	public float getWordWeight(String word) throws TokenNotFound {
		if(words.containsKey(word))
			return words.get(word);
		else
			throw new TokenNotFound("Word not found " +  word);
	}

	public int getWordCount() {
		return words.size();
	}

	public boolean contains(String word) {
		return words.containsKey(word);
	}

	public static SingleFileDictionary fromFilepath(String dictPath) throws IOException {
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
