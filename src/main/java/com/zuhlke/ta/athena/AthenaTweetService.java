package com.zuhlke.ta.athena;

import com.zuhlke.ta.prototype.Query;
import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.prototype.SentimentTimeline.Day;
import com.zuhlke.ta.prototype.Tweet;
import com.zuhlke.ta.prototype.TweetService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.UNORDERED;

public class AthenaTweetService implements TweetService {

    private final SentimentAnalyzer sentimentAnalyzer;
    private final AthenaJdbcClient athenaJdbcClient;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_LOCAL_DATE;

    public AthenaTweetService(SentimentAnalyzer sentimentAnalyzer, AthenaJdbcClient athenaJdbcClient) {
        this.sentimentAnalyzer = sentimentAnalyzer;
        this.athenaJdbcClient = athenaJdbcClient;
    }

    @Override
    public void importTweets(Collection<Tweet> tweets) {
    }

    public class TweetResult {
        private final String query;
        private final List<String> dates;
        private final List<String> goodTweets;
        private final List<String> badTweets;

        TweetResult(String query, List<String> dates, List<String> goodTweets, List<String> badTweets) {
            this.query = query;
            this.dates = dates;
            this.goodTweets = goodTweets;
            this.badTweets = badTweets;
        }

        void append(NeoTweet neoTweet) {
            dates.add(neoTweet.date);
            goodTweets.add(neoTweet.positiveCount);
            badTweets.add(neoTweet.negativeCount);
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

        @Override
        public String toString() {
            return "TweetResult{" +
                "query='" + query + '\'' +
                ", dates=" + dates +
                ", goodTweets=" + goodTweets +
                ", badTweets=" + badTweets +
                '}';
        }
    }

    @Override
    public TweetResult analyzeSentimentOverTime(Query q) {
        final String keyword = q.keyword.toLowerCase();
        final Tracer tracer = new Tracer(q.keyword);
        List<NeoTweet> neoTweets = athenaJdbcClient.selectContentMatching(keyword);

        TweetResult tweetResult = new TweetResult(q.getKeyword(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        neoTweets.forEach(tweetResult::append);

        tracer.summarise();
        return tweetResult;
    }

    private Collector<Tweet, Day, Day> toSentiment() {
        return Collector.of(Day::new, this::interpretSentiment, Day::merge, UNORDERED);
    }

    private void interpretSentiment(Day day, Tweet tweet) {
        addSentiment(day, sentimentAnalyzer.getSentiment(tweet.message));
    }

    private static void addSentiment(Day day, double sentiment) {
        if (sentiment > 0.0) day.incrementGood();
        else day.incrementBad();
    }

    private static class Tracer {
        final long start = System.currentTimeMillis();
        final AtomicLong count = new AtomicLong();
        final String keyword;

        Tracer(String keyword) {
            this.keyword = keyword;
        }

        void increment(@SuppressWarnings("unused") Tweet unused) {
            long n = count.incrementAndGet();
            if (n % 1000 == 0) System.out.println("processed " + n + " tweets for " + keyword);
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

