package com.zuhlke.ta.prototype;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SentimentTimeline {
    private final String query;
    private final Map<String, Day> days;

    public SentimentTimeline(String query) {
        this(query, new LinkedHashMap<>());
    }

    public SentimentTimeline(String query, Map<String, Day> days) {
        this.query = query;
        this.days = days;
    }

    public static class Day {
        public int goodTweets = 0, badTweets = 0;

        public Day() {
            this(0, 0);
        }

        public Day(int goodTweets, int badTweets) {
            this.goodTweets = goodTweets;
            this.badTweets = badTweets;
        }
    }

    public String getQuery() {
        return query;
    }

    public Map<String, Day> getDays() {
        return days;
    }

    @Override
    public String toString() {
        return query + "\n" +
                days.entrySet().stream()
                        .map(e -> e.getKey() + "\t" + e.getValue().goodTweets + "\t" + e.getValue().badTweets)
                        .collect(Collectors.joining("\n"));
    }
}
