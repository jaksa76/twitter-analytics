package com.zuhlke.ta.prototype;

import org.junit.Test;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class ImporterTest {
    @Test
    public void tryImporting() throws Exception {
        Importer importer = new Importer(new TweetService() {
            public SentimentTimeline analyzeSentimentOverTime(Query q) {
                return new SentimentTimeline(q.keyword);
            }

            public void importTweets(Stream<Tweet> tweets) {
                final AtomicLong count = new AtomicLong();
                tweets.peek(t -> count.incrementAndGet() ).forEach(System.out::println);
                
                System.out.println("imported " + count.longValue() + " tweets.");
            }
        });
        importer.importTweetsFrom(new File("minimal_set_tweets.txt"));
    }
}
