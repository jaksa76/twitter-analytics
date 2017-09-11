package com.zuhlke.ta.sentiment.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Dictionary build from the union of several
 * dictionaries
 * 
 * @author hadoop
 *
 */
public class MultipleFileDictionary implements Dictionary {
	
	private final List<Dictionary> dictionaries = new ArrayList<>();

	public MultipleFileDictionary(String ... dictNames) throws IOException {
		for(String dictName: dictNames) {
			dictionaries.add(MappingFileDictionary.fromSingleFile(dictName));
		}
	}

	public float getWordWeight(String word) throws TokenNotFound {
		for(Dictionary dict: dictionaries) {
			try {
				Float wordWeight = dict.getWordWeight(word);
				return wordWeight;
			} catch (TokenNotFound e) {
				// Not found
			}
		}
		throw new TokenNotFound("Word not found " +  word);
	}

	public int getWordCount() {
		int size = 0;
		for(Dictionary dict: dictionaries) {
			size += dict.getWordCount();
		}
		return size;
	}

	public boolean contains(String word) {
		for(Dictionary dict: dictionaries) {
			if (dict.contains(word)) {
				return true;
			}
		}
		return false;
	}
}
