package com.zuhlke.ta.prototype.mapdb;

import com.zuhlke.ta.prototype.*;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

public class MapDBTweetService implements TweetService {
    private final SimpleDateFormat dateFormat;
    private final DB db;
    private final HTreeMap<Object, Tweet> tweets;
    private final SentimentAnalyzer sentimentAnalyzer;

    @SuppressWarnings("unchecked")
    public MapDBTweetService(SentimentAnalyzer sentimentAnalyzer) {
        this.sentimentAnalyzer = sentimentAnalyzer;
        db = DBMaker.fileDB(new File("tweets.db")).make();
        tweets = (HTreeMap<Object, Tweet>) db.hashMap("tweets").createOrOpen();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public void importTweets(Stream<Tweet> tweetStream) {
        tweetStream.forEach(t -> tweets.put(t.id, t));
    }

    @Override
    public SentimentTimeline analyzeSentimetOverTime(Query q) {
        SentimentTimeline result = new SentimentTimeline(q.keyword);

        int n = 0;
        long start = System.currentTimeMillis();
        for (Object id : tweets.keySet()) {
            Tweet t = tweets.get(id);
            if (t.message.contains(q.keyword)) {
                SentimentTimeline.Day day = result.getDays().computeIfAbsent(dateFormat.format(t.date), k -> new SentimentTimeline.Day());

                if (sentimentAnalyzer.getSentiment(t.message) > 0.0) day.goodTweets += 1;
                else day.badTweets += 1;
            }

            n++;
            if (n%10000 == 0) System.out.println("processed " + n + " tweets for " + q.keyword);
        }

        System.out.println("Processed " + (1000 * n / (System.currentTimeMillis() - start)) + " tweets/s");

        return result;
    }

    public void shutdown() {
        db.close();
    }
}
