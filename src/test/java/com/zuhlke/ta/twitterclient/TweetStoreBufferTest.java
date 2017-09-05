package com.zuhlke.ta.twitterclient;

import com.zuhlke.ta.prototype.Tweet;
import com.zuhlke.ta.prototype.TweetStore;
import static org.junit.Assert.*;
import org.junit.Test;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

/**
 * Created by eabi on 04/09/2017.
 */
public class TweetStoreBufferTest {
    @Test
    public void shouldOnlySendTweetsToStoreOnceBufferIsFull() {
        TweetStore store = mock(TweetStore.class);
        int bufferSize = 5;

        TweetStoreBuffer target = new TweetStoreBuffer(store, bufferSize);

        for (int count = 1; count < bufferSize; count++) {
            target.addTweet(createTestTweet());
        }

        verify(store, never()).importTweets(any());

        StreamCountCapture capture = prepareToCaptureSentTweetsCount(store);

        target.addTweet(createTestTweet());

        verify(store, times(1)).importTweets(any());
        assertEquals(bufferSize, capture.getCount());
    }

    @Test
    public void shouldEmptyTheBufferAfterSendingTweets() {
        TweetStore store = mock(TweetStore.class);
        int bufferSize = 5;

        TweetStoreBuffer target = new TweetStoreBuffer(store, bufferSize);

        for (int count = 1; count < 2 * bufferSize; count++) {
            target.addTweet(createTestTweet());
        }

        // Sanity check - should be just before the point the 2nd batch gets sent, so should have already been called just once
        verify(store, times(1)).importTweets(any());
        reset(store);

        StreamCountCapture capture = prepareToCaptureSentTweetsCount(store);

        target.addTweet(createTestTweet());

        verify(store, times(1)).importTweets(any());
        assertEquals(bufferSize, capture.getCount());
    }

    private Tweet createTestTweet() {
        return new Tweet(0, "", "", LocalDate.of(2017, 9, 4));
    }

    private StreamCountCapture prepareToCaptureSentTweetsCount(TweetStore store) {
        StreamCountCapture capture = new StreamCountCapture();

        doAnswer(invocationOnMock -> {
            Stream<Tweet> tweets = (Stream<Tweet>)invocationOnMock.getArguments()[0];
            capture.setCount(tweets.count());
            return null;
        }).when(store).importTweets(any());

        return capture;
    }

    private class StreamCountCapture {
        private long count;

        public void setCount(long count) { this.count = count; }

        public long getCount() { return count; }
    }
}
