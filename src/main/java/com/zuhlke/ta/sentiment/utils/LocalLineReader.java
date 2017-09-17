package com.zuhlke.ta.sentiment.utils;

import java.util.List;
import java.util.stream.Stream;

public class LocalLineReader implements DictionaryLineReader {

	private final String folder;
	
	public LocalLineReader(String folder) {
		super();
		this.folder = folder;
	}

	@Override
	public List<String> readLines(String filename) {
		return FileToListReader.readFile(folder + filename);
	}

	@Override
	public Stream<String> linesFrom(String filename) {
		return FileToListReader.linesFromResourceFile(folder + filename);
	}

}
