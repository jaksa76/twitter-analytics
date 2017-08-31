package com.zuhlke.ta.prototype.inmemory;

import com.zuhlke.ta.prototype.*;
import com.zuhlke.ta.prototype.SentimentTimeline.Day;
import com.zuhlke.ta.sentiment.TwitterSentimentAnalyzerImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        SentimentTimeline result = new SentimentTimeline(q.keyword);

        int n = 0;
        long start = System.currentTimeMillis();
        for (Tweet t : tweets) {
            if (t.message.contains(q.keyword)) {
                Day day = result.getDays().computeIfAbsent(dateFormat.format(t.date), k -> new Day());

                if (sentimentAnalyzer.getSentiment(t.message) > 0.0) day.goodTweets += 1;
                else day.badTweets += 1;
            }

            n++;
            if (n%10000 == 0) System.out.println("processed " + n + " tweets for " + q.keyword);
        }

        System.out.println("Processed " + (1000 * n / (System.currentTimeMillis() - start)) + " tweets/s");

        return result;
    }
}
