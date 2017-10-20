package com.zuhlke.ta.prototype.solutions.inmemory;

import com.zuhlke.ta.prototype.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collector;

import static java.util.stream.Collectors.groupingBy;

public class InMemoryTweetService implements TweetService {
    private final List<Tweet> tweets = new ArrayList<>();
    private final SentimentAnalyzer sentimentAnalyzer;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_LOCAL_DATE;

    public InMemoryTweetService(SentimentAnalyzer sentimentAnalyzer) {
        this.sentimentAnalyzer = sentimentAnalyzer;
    }

    @Override
    public void importTweets(Collection<Tweet> tweets) {
        this.tweets.addAll(tweets);
    }

    @Override
    public SentimentTimeline analyzeSentimentOverTime(Query q) {
        final String keyword = q.keyword.toLowerCase();
        final Tracer tracer = new Tracer(q.keyword);

        final Map<String, SentimentTimeline.Day> days = tweets.stream()
                .peek(tracer::increment)
                .filter(t -> t.message.toLowerCase().contains(keyword))
                .collect(groupingBy(t -> t.date.format(dateFormat), LinkedHashMap::new, toSentiment()));

        tracer.summarise();

        return new SentimentTimeline(q.keyword, days);
    }

    private Collector<Tweet, SentimentTimeline.Day, SentimentTimeline.Day> toSentiment() {
        return Collector.of(SentimentTimeline.Day::new, this::interpretSentiment, SentimentTimeline.Day::merge, Collector.Characteristics.UNORDERED);
    }

    private void interpretSentiment(SentimentTimeline.Day day, Tweet tweet) {
        addSentiment(day, sentimentAnalyzer.getSentiment(tweet.message));
    }

    private static void addSentiment(SentimentTimeline.Day day, double sentiment) {
        if (sentiment > 0.0) day.incrementGood(); else day.incrementBad();
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
            if (count.get() == 0) {
                System.out.println("Processed " + 0 + " tweets/s");
            } else {
                System.out.println("Processed " + (1000 * count.get() / (System.currentTimeMillis() - start)) + " tweets/s");
            }
        }
    }
}
