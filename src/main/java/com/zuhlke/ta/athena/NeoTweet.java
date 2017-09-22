package com.zuhlke.ta.athena;

public class NeoTweet {

    public final String date;
    public final String positiveCount;
    public final String negativeCount;

    private NeoTweet(String date, String positiveCount, String negativeCount) {
        this.date = date;
        this.positiveCount = positiveCount;
        this.negativeCount = "-" + negativeCount;
    }

    public static NeoTweet from(String date, String positiveCount, String negativeCount) {
        return new NeoTweet(date, positiveCount, negativeCount);
    }
}
