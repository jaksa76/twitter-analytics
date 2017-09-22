package com.zuhlke.ta.model;

import java.util.ArrayList;
import java.util.List;

public class TweetSearchResult {

    private final String query;
    private final List<String> dates;
    private final List<String> goodTweets;
    private final List<String> badTweets;

    public TweetSearchResult(String query) {
        this.query = query;
        this.dates = new ArrayList<>();
        this.goodTweets = new ArrayList<>();
        this.badTweets = new ArrayList<>();
    }

    public void append(Tweet tweet) {
        dates.add(tweet.date);
        goodTweets.add(tweet.positiveCount);
        badTweets.add(tweet.negativeCount);
    }

    public String getQuery() {
        return query;
    }

    public List<String> getDates() {
        return dates;
    }

    public List<String> getGoodTweets() {
        return goodTweets;
    }

    public List<String> getBadTweets() {
        return badTweets;
    }
}