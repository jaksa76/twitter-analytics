package com.zuhlke.ta.sentiment.utils;

import java.util.stream.Stream;

/**
 * Reads the lines of a dictionary
 * 
 * @author hadoop
 *
 */
public interface DictionaryLineReader {
	Stream<String> linesFrom(String filename);
}
