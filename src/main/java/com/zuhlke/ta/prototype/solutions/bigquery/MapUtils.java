package com.zuhlke.ta.prototype.solutions.bigquery;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.zuhlke.ta.prototype.SentimentTimeline;

import java.util.Map;
import java.util.TreeMap;

public class MapUtils {
    public static Map<String, SentimentTimeline.Day> merge(Map<String, Integer> goodTweets, Map<String, Integer> badTweets) {
        Map<String, SentimentTimeline.Day> merged = new TreeMap<>();

        MapDifference<String, Integer> difference = Maps.difference(goodTweets, badTweets);
        difference.entriesInCommon().forEach((date, count) -> merged.put(date, new SentimentTimeline.Day(count, count)));
        difference.entriesDiffering().forEach((date, values) -> merged.put(date, new SentimentTimeline.Day(values.leftValue(), values.rightValue())));
        difference.entriesOnlyOnLeft().forEach((date, value) -> merged.put(date, new SentimentTimeline.Day(value, 0)));
        difference.entriesOnlyOnRight().forEach((date, value) -> merged.put(date, new SentimentTimeline.Day(0, value)));

        return merged;
    }
}
