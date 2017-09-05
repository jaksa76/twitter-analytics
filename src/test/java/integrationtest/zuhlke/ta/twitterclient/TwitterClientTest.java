package integrationtest.zuhlke.ta.twitterclient;

import com.zuhlke.ta.prototype.Tweet;
import com.zuhlke.ta.prototype.TweetStore;
import com.zuhlke.ta.prototype.inmemory.InMemoryTweetStore;
import com.zuhlke.ta.twitterclient.*;
import org.junit.Test;
import static org.junit.Assert.*;
import twitter4j.StatusListener;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by eabi on 04/09/2017.
 */
public class TwitterClientTest {
    @Test
    public void shouldCollectTweets() throws InterruptedException {
        InMemoryTweetStore tweetStore = new InMemoryTweetStore();

        TwitterClient client = new TwitterClient(
                new Listener(new TweetStoreBuffer(tweetStore, 10)),
                new LocationBounds(-180.0, -90.0, 180.0, 90.0));

        client.run();

        List<Tweet> collectedTweets = waitForTweets(tweetStore);

        try {
            assertHaveDistinctTweets(collectedTweets);
        } finally {
            client.close();
        }
    }

    private List<Tweet> waitForTweets(TweetStore tweetStore) throws InterruptedException {
        List<Tweet> collectedTweets = tweetStore.tweets().collect(Collectors.toList());
        final int MAX_WAIT_COUNT = 10;
        int waitCount = 0;
        while (waitCount < MAX_WAIT_COUNT && collectedTweets.size() == 0) {
            waitCount++;
            collectedTweets = tweetStore.tweets().collect(Collectors.toList());
            Thread.sleep(1000);
        }

        return collectedTweets;
    }

    private void assertHaveDistinctTweets(List<Tweet> collectedTweets) {
        assertNotEquals(0, collectedTweets.size());

        List<Long> distinctTweetIds = collectedTweets.stream().map(t -> t.id).distinct().collect(Collectors.toList());
        assertEquals(collectedTweets.size(), distinctTweetIds.size());
    }
}
