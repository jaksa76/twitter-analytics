package com.zuhlke.ta.prototype.solutions.inmemory;

import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.prototype.solutions.common.PersistentTweetService;

public class InMemoryTweetService extends PersistentTweetService {
    public InMemoryTweetService(SentimentAnalyzer sentimentAnalyzer) {
        super(sentimentAnalyzer, new InMemoryTweetStore());
    }
}
