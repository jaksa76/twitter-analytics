package com.zuhlke.ta.prototype;

import java.util.List;

public interface TweetService {
    SentimentTimeline analyzeSentimetOverTime(Query q);
    void importTweets(List<Tweet> tweets);
}
