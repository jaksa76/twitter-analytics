package com.zuhlke.ta.sentiment.utils;

import java.util.stream.Stream;

public class LocalLineReader implements DictionaryLineReader {
	private final String folder;

	public LocalLineReader(String folder) {
		this.folder = folder;
	}

	@Override
	public Stream<String> linesFrom(String filename) {
		return FileToListReader.linesFromResourceFile(folder + filename);
	}

}
