package com.zuhlke.ta.prototype;

import java.util.stream.Stream;

public interface TweetService {
    SentimentTimeline analyzeSentimetOverTime(Query q);
    void importTweets(Stream<Tweet> tweets);
}
