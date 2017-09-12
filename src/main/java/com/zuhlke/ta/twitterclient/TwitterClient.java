package com.zuhlke.ta.twitterclient;

import twitter4j.*;
import twitter4j.conf.ConfigurationContext;

import java.io.Closeable;

public class TwitterClient implements Closeable {
    private final StatusListener listener;
    private final LocationBounds locationBounds;

    private TwitterStream stream;

    public TwitterClient(StatusListener listener, LocationBounds locationBounds) {
        this.listener = listener;
        this.locationBounds = locationBounds;
    }

    public void run() {
        final Twitter twitter = new TwitterFactory(ConfigurationContext.getInstance()).getInstance();

        stream = new TwitterStreamFactory().getInstance(twitter.getAuthorization());

        stream.addListener(listener);
        stream.filter(new FilterQuery().locations(locationBounds.toLocationsArray()));
    }

    @Override
    public void close() {
        System.out.println("Closing Twitter stream session");
        stream.shutdown();
    }
}
