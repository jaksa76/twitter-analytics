package com.zuhlke.ta.twitterclient;

import com.zuhlke.ta.prototype.Tweet;
import com.zuhlke.ta.prototype.TweetStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eabi on 04/09/2017.
 */
public class TweetStoreBuffer implements TweetSink {
    private final TweetStore tweetStore;
    private final int bufferSize;
    private final List<Tweet> buffer = new ArrayList<>();

    public TweetStoreBuffer(TweetStore tweetStore, int bufferSize) {
        this.tweetStore = tweetStore;
        this.bufferSize = bufferSize;
    }

    @Override
    public void addTweet(Tweet tweet) {
        buffer.add(tweet);

        if (buffer.size() >= bufferSize) {
            System.out.println("Sending tweets...");
            tweetStore.importTweets(buffer.stream());
            buffer.clear();
        }
    }
}
