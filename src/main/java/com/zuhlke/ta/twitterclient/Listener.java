package com.zuhlke.ta.twitterclient;

import com.zuhlke.ta.prototype.Tweet;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Created by eabi on 04/09/2017.
 */
public class Listener implements StatusListener {
    private final TweetSink sink;

    public Listener(TweetSink sink) {
        this.sink = sink;
    }

    @Override
    public void onStatus(Status status) {
        LocalDate tweetDate = status.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Tweet tweet = new Tweet(
                status.getId(),
                status.getUser().getScreenName(),
                status.getText(),
                tweetDate
        );

        System.out.print("+");
//                System.out.println(String.format("user=%s\tmessage='%s'", tweet.userId, tweet.message));

        sink.addTweet(tweet);
    }

    @Override
    public void onException(Exception e) {
        System.err.println(e.toString());
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
    }

    @Override
    public void onTrackLimitationNotice(int i) {
    }

    @Override
    public void onScrubGeo(long l, long l1) {
    }

    @Override
    public void onStallWarning(StallWarning stallWarning) {
    }
}
