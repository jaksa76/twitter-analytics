package com.zuhlke.ta.sentiment.pipeline.impl;

import com.zuhlke.ta.sentiment.model.Intensifier;
import com.zuhlke.ta.sentiment.model.WeightedWord;
import com.zuhlke.ta.sentiment.pipeline.Enhancer;
import com.zuhlke.ta.sentiment.utils.*;

import java.io.IOException;
import java.util.List;

import static com.zuhlke.ta.sentiment.utils.POSUtils.stripWord;
import static java.lang.Math.max;



public class IntensifiersEnhancer implements Enhancer {

	private Dictionary intensifiers;
	private static int SCOPE_LEN = 6;
	
	public IntensifiersEnhancer() throws IOException {
		try {
			intensifiers = Dictionaries.singleFileDictionaryFrom(DictionaryConstans.INTENSIFIERS_FILE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * For each sentiment word found, it finds potential 
	 * intensifiers that can modify 
	 */
	public List<WeightedWord> enhance(List<WeightedWord> words) {
		for (int i = 0; i < words.size(); i++) {
			final WeightedWord word = words.get(i);

			if(word.isOpinionWord()){
				for (int j = max(0, i-1); j >= max(0, i-SCOPE_LEN); j--) {
					final WeightedWord previousWord = words.get(j);
					if (isIntensifier(previousWord)) {
						// There are words that can act as sentiment words or
						// as intensifiers, change the role!!
						previousWord.setOpinionWord(false);
						previousWord.setSentimentDegree(0);
						
						// Add intensifier
						word.addIntensifier(createIdentifier(previousWord));
					}else if(isStopMark(previousWord)){
						break;
					}
				}
			}
		}
		
		return words;
	}
	
	private boolean isStopMark(WeightedWord word){
		return word.isLimitWord();
	}
	
	private Boolean isIntensifier(WeightedWord word) {
		return intensifiers.contains(stripWord(word.getWord()));
	}
	
	private Intensifier createIdentifier(WeightedWord word) {
		try {
			String ww = stripWord(word.getWord());
			return new Intensifier(ww, intensifiers.getWordWeight(ww));
		} catch (TokenNotFound e) {
			return null;
		}
	}
}
