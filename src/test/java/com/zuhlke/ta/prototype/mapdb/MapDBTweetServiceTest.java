package com.zuhlke.ta.prototype.mapdb;

import com.zuhlke.ta.prototype.Importer;
import com.zuhlke.ta.prototype.Query;
import com.zuhlke.ta.prototype.SentimentTimeline;
import com.zuhlke.ta.sentiment.TwitterSentimentAnalyzerImpl;
import org.junit.After;
import org.junit.Test;

import java.io.File;

public class    MapDBTweetServiceTest {

    private MapDBTweetService tweetService;

    @After
    public void tearDown() {
        if (tweetService != null) tweetService.shutdown();
    }

    @Test
    public void testImportingTweets() throws Exception {
        tweetService = new MapDBTweetService(new TwitterSentimentAnalyzerImpl());
        Importer importer = new Importer(tweetService);
        importer.importTweetsFrom(new File("test_set_tweets.txt"));
        tweetService.shutdown();
    }

    @Test
    public void testAnalyzingSentiment() throws Exception {
        MapDBTweetService tweetService = new MapDBTweetService(new TwitterSentimentAnalyzerImpl());
        SentimentTimeline timeline = tweetService.analyzeSentimetOverTime(new Query(""));
        System.out.println(timeline);
        tweetService.shutdown();
    }
}
