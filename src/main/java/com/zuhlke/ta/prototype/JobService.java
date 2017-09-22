package com.zuhlke.ta.prototype;

import com.zuhlke.ta.athena.AthenaTweetService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class JobService extends Thread {
    private final List<AthenaTweetService.TweetResult> results = Collections.synchronizedList(new ArrayList<>());
    private final BlockingQueue<Query> pendingQueries = new LinkedBlockingQueue<>();
    private TweetService tweetService;

    public JobService(TweetService tweetService) {
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

    public List<AthenaTweetService.TweetResult> getResults() {
        System.out.println("getResults");
        return results;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            while (true) {
                Optional.ofNullable(pendingQueries.poll(1, TimeUnit.SECONDS))
                        .map(q -> tweetService.analyzeSentimentOverTime(q))
                        .ifPresent(results::add);
            }
        } catch (InterruptedException e) {
            // this is ok
        }
    }

    public List<Query> getPending() {
        System.out.println("getPending");
        return new ArrayList<>(pendingQueries);
    }
}
