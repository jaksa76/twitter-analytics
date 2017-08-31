package com.zuhlke.ta.prototype;

/**
 * Created by jvu on 08/02/2017.
 */
public class DummySentimentAnalyzer implements SentimentAnalyzer {
    @Override
    public float getSentiment(String text) {
        return (float) (text.length() * .8 % 2.0 - 1.0);
    }
}
