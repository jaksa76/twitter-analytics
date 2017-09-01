package com.zuhlke.ta.prototype.inmemory;

import com.zuhlke.ta.prototype.*;
import com.zuhlke.ta.prototype.SentimentTimeline.Day;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import static java.util.stream.Collectors.groupingBy;

public class InMemoryTweetService implements TweetService {
    private final SimpleDateFormat dateFormat;
    private final SentimentAnalyzer sentimentAnalyzer;
    private final List<Tweet> tweets = new ArrayList<>();

    public InMemoryTweetService(SentimentAnalyzer sentimentAnalyzer) {
        this.sentimentAnalyzer = sentimentAnalyzer;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public void importTweets(List<Tweet> tweets) {
        this.tweets.addAll(tweets);
    }

    @Override
    public SentimentTimeline analyzeSentimetOverTime(Query q) {
        final String keyword = q.keyword.toLowerCase();
        final Tracer tracer = new Tracer(q.keyword);

        final Map<String, Day> days = tweets.stream()
                .filter(t -> t.message.toLowerCase().contains(keyword))
                .peek(tracer::increment)
                .collect(groupingBy(t -> dateFormat.format(t.date), LinkedHashMap::new, toSentiment()));

        tracer.summarise();

        return new SentimentTimeline(q.keyword, days);
    }

    private Collector<Tweet, Day, Day> toSentiment() {
        return Collector.of(
                Day::new,
                (day, tweet) -> { if (sentimentAnalyzer.getSentiment(tweet.message) > 0.0) day.goodTweets += 1; else day.badTweets += 1; },
                (day1, day2) -> {
                    day1.goodTweets += day2.goodTweets;
                    day1.badTweets += day2.badTweets;
                    return day1;
                },
                Characteristics.UNORDERED);
    }

    static class Tracer {
        final long start = System.currentTimeMillis();
        final AtomicLong count = new AtomicLong();
        final String keyword;

        Tracer(String keyword) { this.keyword = keyword; }

        void increment(Tweet unused) {
            long n = count.incrementAndGet();
            if (n%1000 == 0) System.out.println("processed " + n + " tweets for " + keyword);
        }

        void summarise() {
            System.out.println("Processed " + (1000 * count.get() / (System.currentTimeMillis() - start)) + " tweets/s");
        }
    }
}
