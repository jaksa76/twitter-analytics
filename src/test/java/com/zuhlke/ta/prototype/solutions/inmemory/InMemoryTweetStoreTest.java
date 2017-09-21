package com.zuhlke.ta.prototype.solutions.inmemory;

import com.zuhlke.ta.prototype.Tweet;
import org.junit.Test;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTweetStoreTest {
    @Test
    public void should_be_able_to_append_new_tweets_whilst_processing() {
        InMemoryTweetStore target = new InMemoryTweetStore();

        target.importTweets(Stream.of(anyTweet(), anyTweet()));

        target.tweets()
                .peek(t -> target.importTweets(Stream.of(anyTweet()))) // Whilst iterating the existing items insert new ones
                .collect(Collectors.toList()); // Should not get a ConcurrentModificationException when collecting
    }

    private Tweet anyTweet() {
        return new Tweet(0, "userid", "message", LocalDate.now());
    }
}
