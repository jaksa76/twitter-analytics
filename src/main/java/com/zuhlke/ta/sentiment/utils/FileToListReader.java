package com.zuhlke.ta.sentiment.utils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("WeakerAccess")
public class FileToListReader {
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
