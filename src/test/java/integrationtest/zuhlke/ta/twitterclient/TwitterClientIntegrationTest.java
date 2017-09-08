package integrationtest.zuhlke.ta.twitterclient;

import com.zuhlke.ta.prototype.Tweet;
import com.zuhlke.ta.prototype.inmemory.InMemoryTweetStore;
import com.zuhlke.ta.twitterclient.Listener;
import com.zuhlke.ta.twitterclient.LocationBounds;
import com.zuhlke.ta.twitterclient.TwitterClient;
import org.junit.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Stream.concat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class TwitterClientIntegrationTest {
    private final InMemoryTweetStore tweetStore = new InMemoryTweetStore();

    @Test
    public void collectsTweets() throws InterruptedException {
        try (TwitterClient client = new TwitterClient(new Listener(tweetStore), new LocationBounds(-180.0, -90.0, 180.0, 90.0))) {
            client.run();

            waitForTweets()
                    .collect(groupingBy(tweet -> tweet.id))
                    .values()
                    .forEach(tweetsWithSameId -> assertThat(tweetsWithSameId, hasSize(1)));
        }
    }

    private Stream<Tweet> waitForTweets() throws InterruptedException {
        return Optional.ofNullable(tweetStore.poll(10))
                .map(this::allAvailableTweets)
                .orElseThrow(() -> new AssertionError("No tweets collected"));
    }

    private Stream<Tweet> allAvailableTweets(Tweet polledTweet) {
        return concat(Stream.of(polledTweet), tweetStore.tweets());
    }

}
