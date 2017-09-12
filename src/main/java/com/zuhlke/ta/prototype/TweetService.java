package com.zuhlke.ta.prototype;

import java.util.Collection;

public interface TweetService {
    SentimentTimeline analyzeSentimetOverTime(Query q);
    void importTweets(Collection<Tweet> tweets);
}
