package com.zuhlke.ta.prototype;

import org.junit.Test;

import java.io.File;
import java.util.List;

public class ImporterTest {
    @Test
    public void testImporting() throws Exception {
        Importer importer = new Importer(new TweetService() {
            public SentimentTimeline analyzeSentimetOverTime(Query q) {
                return new SentimentTimeline(q.keyword);
            }

            public void importTweets(List<Tweet> tweets) {
                System.out.println("imported " + tweets.size() + " tweets.");
            }
        });
        importer.importTweetsFrom(new File("test_set_tweets.txt"));
    }
}