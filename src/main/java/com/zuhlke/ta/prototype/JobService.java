package com.zuhlke.ta.prototype;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class JobService extends Thread {
    private final List<SentimentTimeline> results = Collections.synchronizedList(new ArrayList<>());
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

    public List<SentimentTimeline> getResults() {
        return results;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Query q = pendingQueries.peek();
                if (q != null) {
                    results.add(tweetService.analyzeSentimetOverTime(q));
                    pendingQueries.take();
                } else {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            // this is ok
        }
    }

    public List<Query> getPending() {
        return new ArrayList<>(pendingQueries);
    }
}
