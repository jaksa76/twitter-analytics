package com.zuhlke.ta.prototype.solutions;

import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.prototype.TweetService;
import com.zuhlke.ta.prototype.solutions.bigquery.BigQueryTweetStore;
import com.zuhlke.ta.prototype.solutions.common.PersistentTweetService;

public class TweetServices {
    public static TweetService bigQuery(SentimentAnalyzer sentimentAnalyzer) {
        return new PersistentTweetService(sentimentAnalyzer, BigQueryTweetStore.create());
    }
}
