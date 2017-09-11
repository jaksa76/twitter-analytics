package com.zuhlke.ta.prototype;

import java.util.stream.Stream;

public interface TweetService {
    SentimentTimeline analyzeSentimentOverTime(Query q);
    void importTweets(Stream<Tweet> tweets);
}
