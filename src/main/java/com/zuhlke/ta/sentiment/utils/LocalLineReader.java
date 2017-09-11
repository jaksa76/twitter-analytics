package com.zuhlke.ta.sentiment.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

public class LocalLineReader implements DictionaryLineReader {

	private final String folder;
	
	public LocalLineReader(String folder) {
		this.folder = folder;
	}

	@Override
	public Stream<String> readLines(String filename) throws IOException {
		return Files.lines(new File(folder + filename).toPath());
	}

}
