package integrationtest.zuhlke.ta.prototype;

import com.zuhlke.ta.prototype.solutions.bigquery.BigQueryTweetStore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class BigQueryTweetStoreIntegrationTest {

    @Test public void
    returnsTweetsFromBigQuery() {
        final BigQueryTweetStore tweetStore = BigQueryTweetStore.create();
        assertThat(tweetStore.tweets().findFirst().isPresent(), equalTo(true));
    }
}
