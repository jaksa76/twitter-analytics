package com.zuhlke.ta.twitterclient;

import com.zuhlke.ta.prototype.Tweet;

/**
 * Created by eabi on 04/09/2017.
 */
public interface TweetSink {
    void addTweet(Tweet tweet);
}
