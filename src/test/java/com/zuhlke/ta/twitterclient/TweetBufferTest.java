package com.zuhlke.ta.twitterclient;

import com.zuhlke.ta.prototype.Tweet;
import com.zuhlke.ta.prototype.TweetService;
import static org.junit.Assert.*;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collection;

import static org.mockito.Mockito.*;

/**
 * Created by eabi on 04/09/2017.
 */
public class TweetBufferTest {
    @Test
    public void shouldOnlySendTweetsToServiceOnceBufferIsFull() {
        TweetService service = mock(TweetService.class);
        int bufferSize = 5;

        TweetBuffer target = new TweetBuffer(service, bufferSize);

        for (int count = 1; count < bufferSize; count++) {
            target.addTweet(createTestTweet());
        }

        verify(service, never()).importTweets(any());

        StreamCountCapture capture = prepareToCaptureSentTweetsCount(service);

        target.addTweet(createTestTweet());

        verify(service, times(1)).importTweets(any());
        assertEquals(bufferSize, capture.getCount());
    }

    @Test
    public void shouldEmptyTheBufferAfterSendingTweets() {
        TweetService service = mock(TweetService.class);
        int bufferSize = 5;

        TweetBuffer target = new TweetBuffer(service, bufferSize);

        for (int count = 1; count < 2 * bufferSize; count++) {
            target.addTweet(createTestTweet());
        }

        // Sanity check - should be just before the point the 2nd batch gets sent, so should have already been called just once
        verify(service, times(1)).importTweets(any());
        reset(service);

        StreamCountCapture capture = prepareToCaptureSentTweetsCount(service);

        target.addTweet(createTestTweet());

        verify(service, times(1)).importTweets(any());
        assertEquals(bufferSize, capture.getCount());
    }

    private Tweet createTestTweet() {
        return new Tweet(0, "", "", LocalDate.of(2017, 9, 4));
    }

    private StreamCountCapture prepareToCaptureSentTweetsCount(TweetService service) {
        StreamCountCapture capture = new StreamCountCapture();

        doAnswer(invocationOnMock -> {
            Collection<Tweet> tweets = (Collection<Tweet>)invocationOnMock.getArguments()[0];
            capture.setCount(tweets.size());
            return null;
        }).when(service).importTweets(any());

        return capture;
    }

    private class StreamCountCapture {
        private long count;

        public void setCount(long count) { this.count = count; }

        public long getCount() { return count; }
    }
}
