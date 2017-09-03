package com.zuhlke.ta.prototype.inmemory;

import com.zuhlke.ta.prototype.*;
import com.zuhlke.ta.prototype.SentimentTimeline.Day;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class InMemoryTweetService implements TweetService {
    private final SentimentAnalyzer sentimentAnalyzer;
    private final List<Tweet> tweets = new ArrayList<>();
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_LOCAL_DATE;

    public InMemoryTweetService(SentimentAnalyzer sentimentAnalyzer) {
        this.sentimentAnalyzer = sentimentAnalyzer;
    }

    @Override
    public void importTweets(Stream<Tweet> tweets) {
        this.tweets.addAll(tweets.collect(toList()));
    }

    @Override
    public SentimentTimeline analyzeSentimetOverTime(Query q) {
        final String keyword = q.keyword.toLowerCase();
        final Tracer tracer = new Tracer(q.keyword);

        final Map<String, Day> days = tweets.stream()
                .filter(t -> t.message.toLowerCase().contains(keyword))
                .peek(tracer::increment)
                .collect(groupingBy(t -> t.date.format(dateFormat), LinkedHashMap::new, toSentiment()));

        tracer.summarise();

        return new SentimentTimeline(q.keyword, days);
    }

    private Collector<Tweet, Day, Day> toSentiment() {
        return Collector.of(Day::new, this::interpretSentiment, Day::merge, Characteristics.UNORDERED);
    }

    private void interpretSentiment(Day day, Tweet tweet) {
        addSentiment(day, sentimentAnalyzer.getSentiment(tweet.message));
    }

    private static void addSentiment(Day day, float sentiment) {
        if (sentiment > 0.0) day.goodTweets += 1; else day.badTweets += 1;
    }

    private static class Tracer {
        final long start = System.currentTimeMillis();
        final AtomicLong count = new AtomicLong();
        final String keyword;

        Tracer(String keyword) { this.keyword = keyword; }

        void increment(@SuppressWarnings("unused") Tweet unused) {
            long n = count.incrementAndGet();
            if (n%1000 == 0) System.out.println("processed " + n + " tweets for " + keyword);
        }

        void summarise() {
            System.out.println("Processed " + (1000 * count.get() / (System.currentTimeMillis() - start)) + " tweets/s");
        }
    }
}
