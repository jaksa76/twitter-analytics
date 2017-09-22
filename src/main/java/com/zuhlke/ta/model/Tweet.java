package com.zuhlke.ta.model;

public class Tweet {

    public final String date;
    public final String positiveCount;
    public final String negativeCount;

    private Tweet(String date, String positiveCount, String negativeCount) {
        this.date = date;
        this.positiveCount = positiveCount;
        this.negativeCount = "-" + negativeCount;
    }

    public static Tweet from(String date, String positiveCount, String negativeCount) {
        return new Tweet(date, positiveCount, negativeCount);
    }
}
