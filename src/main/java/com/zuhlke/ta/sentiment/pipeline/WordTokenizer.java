package com.zuhlke.ta.sentiment.pipeline;

import java.util.List;

/**
 * Find all the tokens present in
 * a sentence. 
 * 
 * @author hadoop
 *
 */
public interface WordTokenizer {

	List<String> tokenize(String sentence);
}
