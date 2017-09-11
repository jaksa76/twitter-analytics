package com.zuhlke.ta.twitterclient;

import com.zuhlke.ta.prototype.TweetStore;

import java.io.IOException;
import java.util.Properties;

public class TwitterClientRunner {
    public static void runClient(TweetStore tweetStore) throws IOException {
        Properties props = new Properties();
        props.load(TwitterClientRunner.class.getClassLoader().getResourceAsStream("config.properties"));

        LocationBounds bounds = new LocationBounds(
                Double.parseDouble(props.getProperty("boundsLatitudeMin")),
                Double.parseDouble(props.getProperty("boundsLongitudeMin")),
                Double.parseDouble(props.getProperty("boundsLatitudeMax")),
                Double.parseDouble(props.getProperty("boundsLongitudeMax")));

        final TwitterClient client = new TwitterClient(new Listener(tweetStore), bounds);

        client.run();

        Runtime.getRuntime().addShutdownHook(new Thread(client::close));
    }
}
