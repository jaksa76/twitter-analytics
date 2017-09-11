package com.zuhlke.ta.prototype;

public class DummySentimentAnalyzer implements SentimentAnalyzer {
    @Override
    public double getSentiment(String text) {
        return (float) (text.length() * .8 % 2.0 - 1.0);
    }
}
