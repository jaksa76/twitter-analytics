package com.zuhlke.ta.prototype.solutions.inmemory;

import com.zuhlke.ta.prototype.Tweet;
import com.zuhlke.ta.prototype.solutions.common.TweetStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class InMemoryTweetStore implements TweetStore {
    private final List<Tweet> tweets = new ArrayList<>();

    @Override
    public synchronized void importTweets(Stream<Tweet> tweets) {
        this.tweets.addAll(tweets.collect(toList()));
    }

    @Override
    public synchronized Stream<Tweet> tweets() {
        // Make a copy of the tweets so we can still append new ones whilst the analysis is running
        return new ArrayList<>(tweets).stream();
    }
}
