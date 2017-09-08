package com.zuhlke.ta.prototype.inmemory;

import com.zuhlke.ta.prototype.Tweet;
import com.zuhlke.ta.prototype.TweetStore;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class InMemoryTweetStore implements TweetStore {
    private final LinkedBlockingQueue<Tweet> tweets = new LinkedBlockingQueue<>();


    @Override
    public void addTweet(Tweet tweet) {
        tweets.add(tweet);
    }

    @Override
    public Stream<Tweet> tweets() {
        return tweets.stream();
    }

    @SuppressWarnings("SameParameterValue")
    public Tweet poll(int timeoutSeconds) throws InterruptedException {
        return tweets.poll(timeoutSeconds, TimeUnit.SECONDS);
    }
}
