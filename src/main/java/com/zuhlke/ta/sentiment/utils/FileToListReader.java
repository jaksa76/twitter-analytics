package com.zuhlke.ta.sentiment.utils;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FileToListReader {
	public static List<String> readFile(String fileName) {

		List<String> out = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(FileToListReader.class.getClassLoader().getResourceAsStream(fileName)));
			String line = null;
			while((line = reader.readLine()) != null)
				out.add(line);
			return out;
		} catch (IOException e) {
			return out;
		}
	}

	public static Stream<String> linesFromResourceFile(String fileName) {
		final LineReadingSpliterator lineReading = new LineReadingSpliterator(resourceReaderFor(fileName), fileName);
		return StreamSupport.stream(lineReading, false)
				.onClose(lineReading::close);

	}

	@NotNull
	private static InputStreamReader resourceReaderFor(String fileName) {
		return new InputStreamReader(FileToListReader.class.getClassLoader().getResourceAsStream(fileName));
	}

	@SuppressWarnings("WeakerAccess")
	private static class LineReadingSpliterator extends Spliterators.AbstractSpliterator<String> {
		private final BufferedReader reader;
		private final String fileName;

		public LineReadingSpliterator(Reader reader, String fileName) {
			super(Long.MAX_VALUE, Spliterator.ORDERED);
			this.reader = new BufferedReader(reader);
			this.fileName = fileName;
		}

		@Override
        public boolean tryAdvance(Consumer<? super String> action) {
            try {
                final String line = reader.readLine();
                if (line != null) {
                    action.accept(line);
                    return true;
                }
            } catch (IOException e) {
                throw new EnvironmentError("reading from " + fileName, e);
            }
            return false;
        }

		public void close() {
			try {
				reader.close();
			} catch (IOException e) {
				throw new EnvironmentError("closing " + fileName, e);
			}
		}
	}
}
