package com.zuhlke.ta.sentiment.pipeline.impl;

import com.zuhlke.ta.sentiment.model.WeightedWord;
import com.zuhlke.ta.sentiment.pipeline.SentimentWordFinder;
import com.zuhlke.ta.sentiment.utils.*;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.SimpleStemmer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zuhlke.ta.sentiment.utils.POSUtils.*;

public class SentimentWordFinderImpl implements SentimentWordFinder {
	
	private final Dictionary nounsDictionary;
	private final Dictionary adjDictionary;
	private final Dictionary advDictionary;
	private final Dictionary verbDictionary;
	//private final edu.mit.jwi.Dictionary dict;
	private final SimpleStemmer stemmer;
	
	public SentimentWordFinderImpl() throws IOException, URISyntaxException {
		this.nounsDictionary = MappingFileDictionary.fromSingleFile(DictionaryConstans.NOUNS_FILE);
		this.adjDictionary = MappingFileDictionary.fromSingleFile(DictionaryConstans.ADJECTIVES_FILE);
		this.advDictionary = MappingFileDictionary.fromSingleFile(DictionaryConstans.ADVERBS_FILE);
		this.verbDictionary = MappingFileDictionary.fromSingleFile(DictionaryConstans.VERBS_FILE);

        //URL dir_url = ClassLoader.getSystemResource("wordnet");
        //File file = new File(dir_url.toURI());
		
		//dict = new edu.mit.jwi.Dictionary(file);
		//dict.open();
		//stemmer = new WordnetStemmer(dict);
        // TODO: try to solve the issue
        stemmer = new SimpleStemmer();
	}
	
	public List<WeightedWord> find(String... words) {
		ArrayList<WeightedWord> result = new ArrayList<>();
		
		for (String word : words) {
			WeightedWord weightedWord = new WeightedWord(word, 1);

            String originalWord = word;

            List<String> stems = new ArrayList<>();
			try{
				if(isVerb(word)){
					word = stripWord(word);
					if(!word.isEmpty()){
						stems.addAll(stemmer.findStems(word, POS.VERB));
                    	stems.add(word);
                    	weightedWord.setSentimentDegree(findWeight(verbDictionary, stems));
                    	weightedWord.setOpinionWord(true);
					}
				}else if(isNoun(word)){
					word = stripWord(word);
					if(!word.isEmpty()){
						stems.addAll(stemmer.findStems(word, POS.NOUN));
                    	stems.add(word);
                    	weightedWord.setSentimentDegree(findWeight(nounsDictionary, stems));
                    	weightedWord.setOpinionWord(true);
					}
				}else if(isAdjective(word)){
					word = stripWord(word);
					if(!word.isEmpty()){
						stems.addAll(stemmer.findStems(word, POS.ADJECTIVE));
						stems.add(word);
						weightedWord.setSentimentDegree(findWeight(adjDictionary, stems));
						weightedWord.setOpinionWord(true);
					}
				}else if(isAdverb(word)){
					word = stripWord(word);
					if(!word.isEmpty()){
						stems.addAll(stemmer.findStems(word, POS.ADVERB)); // SimpleStemmer no rule for adverb
	                    stems.add(word);
						try {
							weightedWord.setSentimentDegree(findWeight(advDictionary, stems));
							weightedWord.setOpinionWord(true);
						} catch (TokenNotFound e) {
							if(word.endsWith("ly")){ // Search in root adjective
	                            stems.clear();
	                            stems.addAll(stemmer.findStems(word.substring(0, word.length() - 2), POS.ADJECTIVE));
	                            stems.add(word);
	
								weightedWord.setSentimentDegree(findWeight(adjDictionary, stems));
								weightedWord.setOpinionWord(true);
							}
						}
					}
				}else if (isLimitWord(word)){
					word = stripWord(word);
					weightedWord.setLimitWord(true);
					weightedWord.setOpinionWord(false);
					weightedWord.setSentimentDegree(0);
				}
			}catch(TokenNotFound e){
				weightedWord.setOpinionWord(false);
				weightedWord.setSentimentDegree(0);
			}catch(Exception e){
				System.out.println("Problem finding word: " + originalWord);
				weightedWord.setOpinionWord(false);
				weightedWord.setSentimentDegree(0);
			}finally{
				result.add(weightedWord);	
			}
		}
		return result;
	}
	
	private float findWeight(Dictionary dict, List<String> stems) throws TokenNotFound {
		return stems.stream()
				.map(dict::getWordWeight)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst()
				.orElseThrow(() -> new TokenNotFound("no match found"));
	}
}
