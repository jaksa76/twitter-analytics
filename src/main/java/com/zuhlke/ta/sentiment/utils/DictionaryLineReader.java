package com.zuhlke.ta.sentiment.utils;

import java.util.List;
import java.util.stream.Stream;

/**
 * Reads the lines of a dictionary
 * 
 * @author hadoop
 *
 */
public interface DictionaryLineReader {
	List<String> readLines(String filename);
	Stream<String> linesFrom(String filename);
}
