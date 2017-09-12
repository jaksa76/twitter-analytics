package com.zuhlke.ta.twitterclient;

import com.zuhlke.ta.prototype.TweetService;
import twitter4j.StatusListener;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by eabi on 04/09/2017.
 */
public class TwitterClientRunner {
    public static void runClient(TweetService tweetService) throws IOException {
        Properties props = new Properties();
        props.load(TwitterClientRunner.class.getClassLoader().getResourceAsStream("config.properties"));

        LocationBounds bounds = new LocationBounds(
                Double.parseDouble(props.getProperty("boundsLatitudeMin")),
                Double.parseDouble(props.getProperty("boundsLongitudeMin")),
                Double.parseDouble(props.getProperty("boundsLatitudeMax")),
                Double.parseDouble(props.getProperty("boundsLongitudeMax")));

        StatusListener listener = new Listener(new TweetBuffer(
                tweetService,
                Integer.parseInt(props.getProperty("tweetsBufferSize"))));

        TwitterClient client = new TwitterClient(
                listener,
                bounds);

        client.run();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> client.close()));
    }
}
