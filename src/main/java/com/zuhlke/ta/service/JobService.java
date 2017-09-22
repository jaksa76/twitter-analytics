package com.zuhlke.ta.service;

import com.zuhlke.ta.athena.AthenaTweetService;
import com.zuhlke.ta.model.Query;
import com.zuhlke.ta.model.TweetSearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class JobService extends Thread {

    private final List<TweetSearchResult> results = Collections.synchronizedList(new ArrayList<>());
    private final BlockingQueue<Query> pendingQueries = new LinkedBlockingQueue<>();
    private AthenaTweetService tweetService;

    public JobService(AthenaTweetService tweetService) {
        this.tweetService = tweetService;
        this.start();
    }

    public void enqueueQuery(Query q) {
        try {
            pendingQueries.put(q);
        } catch (InterruptedException e) {
            // this is ok
        }
    }

    public List<TweetSearchResult> getResults() {
        return results;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            while (true) {
                Optional.ofNullable(pendingQueries.poll(1, TimeUnit.SECONDS))
                    .map(q -> tweetService.searchFor(q))
                    .ifPresent(results::add);
            }
        } catch (InterruptedException e) {
            // this is ok
        }
    }

    public List<Query> getPending() {
        return new ArrayList<>(pendingQueries);
    }
}
