package com.zuhlke.ta.prototype.mapdb;

import com.zuhlke.ta.prototype.Importer;
import com.zuhlke.ta.prototype.Query;
import com.zuhlke.ta.prototype.SentimentTimeline;
import com.zuhlke.ta.prototype.PersistentTweetService;
import com.zuhlke.ta.sentiment.TwitterSentimentAnalyzerImpl;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.zuhlke.ta.prototype.mapdb.MapDBTweetStore.TWEETS_DB;

public class MapDBTweetServiceTest {
    private final MapDBTweetStore tweetStore = new MapDBTweetStore();
    private final PersistentTweetService tweetService =new PersistentTweetService(
            new TwitterSentimentAnalyzerImpl(),
            tweetStore);

    @After
    public void shutdownTheService() throws IOException {
        tweetStore.close();
        Files.delete(Paths.get(TWEETS_DB));
    }

    @Test
    public void importingAndAnalyzeTweets() throws Exception {
        Importer importer = new Importer(tweetService);
        importer.importTweetsFrom(new File("test_set_tweets.txt"));
        SentimentTimeline timeline = tweetService.analyzeSentimetOverTime(new Query(""));
        System.out.println(timeline);
    }

}
