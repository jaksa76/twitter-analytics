package com.zuhlke.ta.prototype;


import com.zuhlke.ta.athena.AthenaTweetService;

import java.util.Collection;

public interface TweetService {
    AthenaTweetService.TweetResult analyzeSentimentOverTime(Query q);
    void importTweets(Collection<Tweet> tweets);
}
