package com.zuhlke.ta.prototype.solutions.common;

import com.zuhlke.ta.prototype.*;
import com.zuhlke.ta.prototype.SentimentTimeline.Day;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

public class PersistentTweetService implements TweetService {
    private final TweetStore store;
    private final SentimentAnalyzer sentimentAnalyzer;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_LOCAL_DATE;

    public PersistentTweetService(SentimentAnalyzer sentimentAnalyzer, TweetStore tweetStore) {
        this.sentimentAnalyzer = sentimentAnalyzer;
        this.store = tweetStore;
    }

    @Override
    public void importTweets(Collection<Tweet> tweets) {
        store.importTweets(tweets.stream());
    }

    @Override
    public SentimentTimeline analyzeSentimentOverTime(Query q) {
        final String keyword = q.keyword.toLowerCase();
        final Tracer tracer = new Tracer(q.keyword);

        final Map<String, Day> days = store.tweets()
                .peek(tracer::increment)
                .filter(t -> t.message.toLowerCase().contains(keyword))
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

    private static void addSentiment(Day day, double sentiment) {
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
