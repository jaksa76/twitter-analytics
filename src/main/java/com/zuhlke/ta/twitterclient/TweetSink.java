package com.zuhlke.ta.twitterclient;

import com.zuhlke.ta.prototype.Tweet;

public interface TweetSink {
    void addTweet(Tweet tweet);
}
