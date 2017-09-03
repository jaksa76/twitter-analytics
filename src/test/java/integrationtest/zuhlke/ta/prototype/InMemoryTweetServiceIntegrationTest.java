package integrationtest.zuhlke.ta.prototype;

import com.zuhlke.ta.prototype.Importer;
import com.zuhlke.ta.prototype.Query;
import com.zuhlke.ta.prototype.SentimentTimeline;
import com.zuhlke.ta.prototype.inmemory.InMemoryTweetService;
import com.zuhlke.ta.sentiment.TwitterSentimentAnalyzerImpl;
import org.junit.Test;

import java.io.File;

public class InMemoryTweetServiceIntegrationTest {
    @Test
    public void testAnalyzingTweets() throws Exception {
        InMemoryTweetService tweetService = new InMemoryTweetService(new TwitterSentimentAnalyzerImpl());
        Importer importer = new Importer(tweetService);
        importer.importTweetsFrom(new File("test_set_tweets.txt"));
        SentimentTimeline timeline = tweetService.analyzeSentimetOverTime(new Query(""));
        System.out.println(timeline);
    }
}
