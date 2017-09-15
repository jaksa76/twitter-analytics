package com.zuhlke.ta.sentiment.utils;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstract superclass of all the text analyzers which need 
 * sentence detection and tokenization as previous steps
 * 
 * @author dama
 *
 */
public class SentenceDetector {
	private SentenceDetectorME sentenceDetector;	
	private final static String SENTENCE_MODEL = "en-sent.bin";
	private static SentenceDetector instance;

	private SentenceDetector(SentenceDetectorME sentenceDetector) {
		this.sentenceDetector = sentenceDetector;
	}

	public static SentenceDetector fromResource() throws IOException {
		try (InputStream modelResource = SentenceDetector.class.getResourceAsStream(SENTENCE_MODEL)) {
			return new SentenceDetector(new SentenceDetectorME(new SentenceModel(modelResource)));
		}
	}

	public String[] sentencesFrom(String text) {
		return sentenceDetector.sentDetect(text);
	}
	
}
