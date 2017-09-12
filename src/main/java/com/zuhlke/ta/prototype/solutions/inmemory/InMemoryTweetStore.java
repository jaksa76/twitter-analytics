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
    public void importTweets(Stream<Tweet> tweets) {
        this.tweets.addAll(tweets.collect(toList()));
    }

    @Override
    public Stream<Tweet> tweets() {
        return tweets.stream();
    }
}
