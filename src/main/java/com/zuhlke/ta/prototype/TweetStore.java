package com.zuhlke.ta.prototype;

import com.zuhlke.ta.twitterclient.TweetSink;

import java.util.stream.Stream;

public interface TweetStore extends TweetSink {
    Stream<Tweet> tweets();
}
