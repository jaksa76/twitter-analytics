package com.zuhlke.ta.prototype.solutions.mapdb;

import com.zuhlke.ta.prototype.Importer;
import com.zuhlke.ta.prototype.Query;
import com.zuhlke.ta.prototype.SentimentTimeline;
import com.zuhlke.ta.sentiment.TwitterSentimentAnalyzerImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.zuhlke.ta.prototype.solutions.mapdb.MapDBTweetStore.TWEETS_DB;

public class MapDBTweetServiceTest {
    private MapDBTweetService tweetService;

    @Before
    public void setUp() throws Exception {
        new File(TWEETS_DB).delete();
        tweetService = new MapDBTweetService(new TwitterSentimentAnalyzerImpl());
    }

    @Test
    public void importingAndAnalyzeTweets() throws Exception {
        Importer importer = new Importer(tweetService);
        importer.importTweetsFrom(new File("test_set_tweets.txt"));
        SentimentTimeline timeline = tweetService.analyzeSentimentOverTime(new Query("Buhari"));
        System.out.println(timeline);
    }
}
