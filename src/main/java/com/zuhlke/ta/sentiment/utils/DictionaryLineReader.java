package com.zuhlke.ta.sentiment.utils;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Reads the lines of a dictionary
 * 
 * @author hadoop
 *
 */
public interface DictionaryLineReader {
	Stream<String> readLines(String filename) throws IOException;
}
