package com.zuhlke.ta.sentiment.utils;

import java.util.Map;

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

	public SingleFileDictionary(Map<String, Float> words) {
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

}
