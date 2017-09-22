package com.zuhlke.ta.athena;

import com.zuhlke.ta.model.Query;
import com.zuhlke.ta.model.TweetSearchResult;

public class AthenaTweetService {

    private final AthenaJdbcClient athenaJdbcClient;

    public AthenaTweetService(AthenaJdbcClient athenaJdbcClient) {
        this.athenaJdbcClient = athenaJdbcClient;
    }

    public TweetSearchResult searchFor(Query q) {
        String keyword = q.keyword.toLowerCase();
        TweetSearchResult tweetSearchResult = new TweetSearchResult(q.getKeyword());
        athenaJdbcClient.selectContentMatching(keyword)
            .forEach(tweetSearchResult::append);
        return tweetSearchResult;
    }

}

