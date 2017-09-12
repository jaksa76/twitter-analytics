package com.zuhlke.ta.sentiment.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	public Optional<Float> getWordWeight(String word) {
		return dictionaries.stream()
				.map(dict -> dict.getWordWeight(word))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
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
