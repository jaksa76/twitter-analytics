package integrationtest.zuhlke.ta.twitterclient;

import com.zuhlke.ta.prototype.Query;
import com.zuhlke.ta.prototype.SentimentTimeline;
import com.zuhlke.ta.prototype.Tweet;
import com.zuhlke.ta.prototype.TweetService;
import com.zuhlke.ta.twitterclient.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by eabi on 04/09/2017.
 */
public class TwitterClientTest {
    @Test
    public void shouldCollectTweets() throws InterruptedException {
        final List<Tweet> collectedTweets = new ArrayList<>();
        TweetService service = new TweetService() {
            @Override public SentimentTimeline analyzeSentimentOverTime(Query q) { return null; }
            @Override public void importTweets(Collection<Tweet> tweets) { collectedTweets.addAll(tweets); }
        };

        TwitterClient client = new TwitterClient(
                new Listener(new TweetBuffer(service, 10)),
                new LocationBounds(-180.0, -90.0, 180.0, 90.0));

        client.run();

        waitForTweets(collectedTweets);

        try {
            assertHaveDistinctTweets(collectedTweets);
        } finally {
            client.close();
        }
    }

    private List<Tweet> waitForTweets(List<Tweet> collectedTweets) throws InterruptedException {
        final int MAX_WAIT_COUNT = 10;
        int waitCount = 0;
        while (waitCount < MAX_WAIT_COUNT && collectedTweets.size() == 0) {
            waitCount++;
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
