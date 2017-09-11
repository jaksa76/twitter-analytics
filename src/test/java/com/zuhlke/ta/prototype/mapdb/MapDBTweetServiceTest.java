package com.zuhlke.ta.prototype.mapdb;

import com.zuhlke.ta.prototype.Importer;
import com.zuhlke.ta.prototype.PersistentTweetService;
import com.zuhlke.ta.prototype.Query;
import com.zuhlke.ta.sentiment.TwitterSentimentAnalyzerImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.zuhlke.ta.prototype.mapdb.MapDBTweetStore.TWEETS_DB;

public class MapDBTweetServiceTest {

    @Before
    public void cleanupTheStorage() throws IOException {
        Files.deleteIfExists(Paths.get(TWEETS_DB));
    }

    @Test
    public void importingAndAnalyzeTweets() throws Exception {
        try (MapDBTweetStore tweetStore = new MapDBTweetStore()) {
            final PersistentTweetService tweetService = tweetService(tweetStore);

            new Importer(tweetService).importTweetsFrom(new File("minimal_set_tweets.txt"));
            System.out.println(tweetService.analyzeSentimetOverTime(new Query("")));
        }
    }

    @NotNull
    private static PersistentTweetService tweetService(MapDBTweetStore tweetStore) {
        return new PersistentTweetService(
                        new TwitterSentimentAnalyzerImpl(),
                        tweetStore);
    }

}
