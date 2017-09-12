package com.zuhlke.ta.prototype.solutions.mapdb;

import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.prototype.solutions.common.PersistentTweetService;

public class MapDBTweetService extends PersistentTweetService{
    public MapDBTweetService(SentimentAnalyzer sentimentAnalyzer) {
        super(sentimentAnalyzer, new MapDBTweetStore());
    }
}
