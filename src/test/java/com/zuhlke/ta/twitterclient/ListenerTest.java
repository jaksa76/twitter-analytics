package com.zuhlke.ta.twitterclient;

import com.zuhlke.ta.prototype.Tweet;
import org.junit.Test;
import twitter4j.Status;
import twitter4j.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.Mockito.*;

public class ListenerTest {
    private static final long TWEET_ID = 5;
    private static final String TWITTER_USER = "jack";
    private final TweetSink sink = mock(TweetSink.class);
    private final Listener target = new Listener(sink);

    @Test
    public void sends_tweet_to_sink_when_new_status_received() {
        Instant instant = Instant.now();

        String tweetMessage = "Hello, World!";
        LocalDate expectedDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        target.onStatus(createTestStatus(TWEET_ID, TWITTER_USER, tweetMessage, Date.from(instant)));

        verify(sink).addTweet(refEq(new Tweet(TWEET_ID, TWITTER_USER, tweetMessage, expectedDate)));
    }

    private Status createTestStatus(long id, String userScreenName, String text, Date createdAt) {
        User user = mock(User.class);
        when(user.getScreenName()).thenReturn(userScreenName);

        Status status = mock(Status.class);
        when(status.getId()).thenReturn(id);
        when(status.getText()).thenReturn(text);
        when(status.getCreatedAt()).thenReturn(createdAt);
        when(status.getUser()).thenReturn(user);

        return status;
    }
}
