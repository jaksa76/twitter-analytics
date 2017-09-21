package com.zuhlke.ta.sentiment.model;

import java.util.ArrayList;
import java.util.List;

public class WeightedWord {
	private static final int NEGATION_BIAS = 4;
	private final String word;
    private boolean isOpinionWord;
    private boolean isLimitWord;
    private boolean isIntensifier;
	private float sentimentDegree;
	private int length; // It can be a n-gram
	private final List<Intensifier> intensifiers = new ArrayList<>();
	private final List<Negator> negators = new ArrayList<>();

	public WeightedWord(String word, int length) {
		this.word = word;
		this.length = length;
	}

	public double score() {
		float wordValue = intensifiers.stream()
				.map(Intensifier::getIntensity)
				.reduce(sentimentDegree, (l, r) -> l * r);

		for (int i = 0; i < negators.size(); i++) {
            if (wordValue < 0)
                wordValue = wordValue + NEGATION_BIAS;
            else if (wordValue > 0)
                wordValue = wordValue - NEGATION_BIAS;
        }
        return wordValue;
    }


	public int getLength() {
		return length;
	}

	public void markAsItensifier() { this.isIntensifier = true; }

	public boolean isLimitWord() {
		return isLimitWord;
	}

	public void markAsLimitWord() { this.isLimitWord = true; }

	public boolean isOpinionWord() {
		return isOpinionWord;
	}

	public void setOpinionWord(boolean isOpinionWord) {
		this.isOpinionWord = isOpinionWord;
	}

	public void setSentimentDegree(float sentimentDegree) {
		this.sentimentDegree = sentimentDegree;
	}

	public String getWord() {
		return word;
	}

	public void addIntensifier(Intensifier intensifier) {
		intensifiers.add(intensifier);
	}

	public void addNegator(Negator negator) {
		negators.add(negator);
	}


	@Override
	public String toString() {
		return "WeightedWord [word=" + word + ", isOpinionWord="
				+ isOpinionWord + ", isLimitWord=" + isLimitWord
				+ ", isIntensifier=" + isIntensifier + ", sentimentDegree="
				+ sentimentDegree + ", length=" + length + ", intensifiers="
				+ intensifiers + ", negators=" + negators + "]";
	}
}
