package com.zuhlke.ta.prototype.solutions.mapdb;

import com.zuhlke.ta.prototype.Importer;
import com.zuhlke.ta.prototype.Query;
import com.zuhlke.ta.prototype.SentimentTimeline;
import com.zuhlke.ta.sentiment.TwitterSentimentAnalyzerImpl;
import com.zuhlke.ta.sentiment.pipeline.impl.SentimentWordFinderImpl;
import com.zuhlke.ta.sentiment.utils.SentenceDetector;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static com.zuhlke.ta.prototype.solutions.mapdb.MapDBTweetStore.TWEETS_DB;
import static java.nio.file.Files.deleteIfExists;

public class MapDBTweetServiceTest {
    private MapDBTweetService tweetService;

    @Before
    public void setUp() throws Exception {
        deleteIfExists(new File(TWEETS_DB).toPath());
        tweetService = new MapDBTweetService(TwitterSentimentAnalyzerImpl.create(SentenceDetector.fromResource(), SentimentWordFinderImpl.fromDictionaries()));
    }

    @Test
    public void importingAndAnalyzeTweets() throws Exception {
        Importer importer = new Importer(tweetService);
        importer.importTweetsFrom(new File("test_set_tweets.txt"));
        SentimentTimeline timeline = tweetService.analyzeSentimentOverTime(new Query("Buhari"));
        System.out.println(timeline);
    }
}
