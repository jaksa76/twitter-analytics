package com.zuhlke.ta.prototype;

import java.util.stream.Stream;

public interface TweetStore {
    void importTweets(Stream<Tweet> tweets);

    Stream<Tweet> tweets();
}
