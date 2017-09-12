package com.zuhlke.ta.sentiment.pipeline.impl;

import com.zuhlke.ta.sentiment.model.WeightedWord;
import com.zuhlke.ta.sentiment.pipeline.NGramFilter;
import com.zuhlke.ta.sentiment.utils.Dictionary;
import com.zuhlke.ta.sentiment.utils.DictionaryConstans;
import com.zuhlke.ta.sentiment.utils.MappingFileDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.SimpleStemmer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.zuhlke.ta.sentiment.utils.POSUtils.*;


public class NGramFilterImpl implements NGramFilter {
	private int maxL;
	private final Dictionary nounsDictionary;
	private final Dictionary adjDictionary;
	private final Dictionary advDictionary;
	private final Dictionary verbDictionary;
	private final Dictionary intDictionary;

	//private final edu.mit.jwi.Dictionary dict;
	//private final WordnetStemmer stemmer;
    private final SimpleStemmer stemmer;

	public NGramFilterImpl(int maxNgram) throws IOException {
		super();
		this.maxL = maxNgram;
		
		this.nounsDictionary = MappingFileDictionary.fromNgramsFile(DictionaryConstans.NOUNS_NGRAM_FILE);
		this.adjDictionary = MappingFileDictionary.fromNgramsFile(DictionaryConstans.ADJECTIVES_NGRAM_FILE);
		this.advDictionary = MappingFileDictionary.fromNgramsFile(DictionaryConstans.ADVERBS_NGRAM_FILE);
		this.verbDictionary = MappingFileDictionary.fromNgramsFile(DictionaryConstans.VERBS_NGRAM_FILE);
		this.intDictionary = MappingFileDictionary.fromNgramsFile(DictionaryConstans.INTENSIFIERS_NGRAM_FILE);


        stemmer = new SimpleStemmer();
	}

	@Override
	public List<WeightedWord> filterNgrams(List<WeightedWord> words) {
		
		List<WeightedWord> last    = new ArrayList<>();
		List<WeightedWord> current = new ArrayList<>();
		
		// Copy original list as previous
		last.addAll(words);
		
		for(int cNgram = 2; cNgram <= maxL; cNgram++){ // At least bigrams
			current = new ArrayList<>();
			int currPos=0;
			while(currPos < last.size()){
				// Try to generate n-grams out of the current words
				int length = 0;
				int pp = 0;
				List<WeightedWord> nGramTok = new ArrayList<>(cNgram);
				while(length < cNgram && currPos+pp < last.size()){
					WeightedWord word = last.get(currPos+pp);
					length += word.getLength();
					nGramTok.add(word);
					pp++;
				}
				
				if(length == cNgram){
					StringBuilder builder =  new StringBuilder();
					for (WeightedWord token : nGramTok)
						builder.append(lemmatize(token.getWord())).append(" ");
					// Search the ngram					
					WeightedWord weightedNgram = findNgram(builder.toString().trim().toLowerCase(), length);
					
					if(weightedNgram != null){ // we found an ngram, substitute the group
						current.add(weightedNgram);
						currPos += nGramTok.size();
					}else{ // Maintain the word
						current.add(last.get(currPos));
						currPos++;
					}
				}else{ // Ngram not found, go to the next
					current.add(last.get(currPos));
					currPos++;
				}
			}
			
			// Clear last
			last.clear();
			last.addAll(current);
		}
			
		return current;
	}
	
	private WeightedWord findNgram(String ngram, int length){
		return nounsDictionary.getWordWeight(ngram).map(w -> {
				WeightedWord ww = new WeightedWord(ngram, length);
				ww.setOpinionWord(true);
				ww.setSentimentDegree(w);
				return ww;
			})
			.orElseGet(() ->
				adjDictionary.getWordWeight(ngram).map( w -> {
					final WeightedWord ww = new WeightedWord(ngram, length);
					ww.setOpinionWord(true);
					ww.setSentimentDegree(w);
					return ww;
				})
				.orElseGet(() ->
					advDictionary.getWordWeight(ngram).map( w -> {
						final WeightedWord ww = new WeightedWord(ngram, length);
						ww.setOpinionWord(true);
						ww.setSentimentDegree(w);
						return ww;
					})
					.orElseGet(() ->
						verbDictionary.getWordWeight(ngram).map( w -> {
							final WeightedWord ww = new WeightedWord(ngram, length);
							ww.setOpinionWord(true);
							ww.setSentimentDegree(w);
							return ww;
						})
						.orElseGet(() ->
							intDictionary.getWordWeight(ngram).map( w -> {
								final WeightedWord ww = new WeightedWord(ngram, length);
								ww.setOpinionWord(true);
								ww.setSentimentDegree(w);
								ww.setIntensifier(true);
								return ww;
							}).orElse(null)))));
	}
	
	private String lemmatize(String word){
		POS partofSpeech;
		
		if(isVerb(word)){
			partofSpeech = POS.VERB;
		}else if(isNoun(word)){
			partofSpeech = POS.NOUN;
		}else if(isAdjective(word)){
			partofSpeech = POS.ADJECTIVE;
		}else if(isAdverb(word)){
			partofSpeech = POS.ADVERB;
		}else{
			return stripWord(word); // It is an ngram, does not need lematization
		}

        word = stripWord(word);
        if(!word.isEmpty()){
	        List<String> stems = new ArrayList<>();
	        stems.addAll(stemmer.findStems(word, partofSpeech));
	        stems.add(word);
	
			return stems.get(0); // Return the first stemming
        }else{
        	return "";
        }
	}
}
