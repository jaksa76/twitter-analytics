package com.zuhlke.ta.prototype.solutions.common;

import com.zuhlke.ta.prototype.Tweet;

import java.util.stream.Stream;

public interface TweetStore {
    void importTweets(Stream<Tweet> tweets);

    Stream<Tweet> tweets();
}
