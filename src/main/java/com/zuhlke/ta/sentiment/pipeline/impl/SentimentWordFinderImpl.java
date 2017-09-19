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

import static com.zuhlke.ta.sentiment.utils.POSUtils.*;

public class SentimentWordFinderImpl implements SentimentWordFinder {
	//private final edu.mit.jwi.Dictionary dict;
	private final SimpleStemmer stemmer = new SimpleStemmer();

	private final Dictionary nounsDictionary;
	private final Dictionary adjDictionary;
	private final Dictionary advDictionary;
	private final Dictionary verbDictionary;

	private SentimentWordFinderImpl(Dictionary nounsDictionary, Dictionary adjDictionary, Dictionary advDictionary, Dictionary verbDictionary) {
		this.nounsDictionary = nounsDictionary;
		this.adjDictionary = adjDictionary;
		this.advDictionary = advDictionary;
		this.verbDictionary = verbDictionary;

        //URL dir_url = ClassLoader.getSystemResource("wordnet");
        //File file = new File(dir_url.toURI());
		
		//dict = new edu.mit.jwi.Dictionary(file);
		//dict.open();
		//stemmer = new WordnetStemmer(dict);
        // TODO: try to solve the issue
	}

	public static SentimentWordFinderImpl fromDictionaries() throws IOException, URISyntaxException {
		return new SentimentWordFinderImpl(Dictionaries.singleFileDictionaryFrom(DictionaryConstans.NOUNS_FILE), Dictionaries.singleFileDictionaryFrom(DictionaryConstans.ADJECTIVES_FILE), Dictionaries.singleFileDictionaryFrom(DictionaryConstans.ADVERBS_FILE), Dictionaries.singleFileDictionaryFrom(DictionaryConstans.VERBS_FILE));
	}

	public List<WeightedWord> find(String... words) {
		ArrayList<WeightedWord> result = new ArrayList<WeightedWord>();
		
		for (String word : words) {
			WeightedWord weightedWord = new WeightedWord(word, 1);

            String originalWord = word;

            List<String> stems = new ArrayList<String>();
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
	
	private float findWeight(Dictionary dict, List<String> stems) throws TokenNotFound{
		float out = 0.0f;

		for (int i = 0; i < stems.size(); i++) {
			try {
				out = dict.getWordWeight(stems.get(i));
				return out;
			} catch (TokenNotFound e) {
				// skip
			}
		}

		throw new TokenNotFound("no match found");
	}	
}
