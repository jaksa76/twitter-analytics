package com.zuhlke.ta.prototype.inmemory;

import com.zuhlke.ta.prototype.Query;
import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.prototype.SentimentTimeline;
import com.zuhlke.ta.prototype.Tweet;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsEqual.equalTo;

public class InMemoryTweetServiceTest {
    private final Random random = new Random();
    private final Map<String, Float> sentimentResults = new HashMap<>();
    private final SentimentAnalyzer analyzer = text ->
            sentimentResults.entrySet().stream()
            .filter(e -> text.contains(e.getKey()))
            .findFirst()
            .map(Map.Entry::getValue)
            .orElseThrow(() -> new AssertionError("missing sentiment for " + text));
    private final PersistentTweetService service = new PersistentTweetService(analyzer, new InMemoryTweetStore());

    @SuppressWarnings("unchecked")
    @Test
    public void collectsSentimentFromTweets() {
        service.importTweets(Stream.of(
                tweet("one keyword", 5, 15), tweet("two keyword", 5, 15),
                tweet("three keyword", 6, 16), tweet("four keyword", 6, 16)));
        sentimentResults.put("one", 1.2f);
        sentimentResults.put("two", 2.3f);
        sentimentResults.put("three", -2.3f);
        sentimentResults.put("four", 0.0f);

        final SentimentTimeline timeline = service.analyzeSentimetOverTime(new Query("Keyword"));

        assertThat(timeline.getQuery(), equalTo("Keyword"));
        assertThat(timeline.getDays(),
                    allOf(hasEntry(equalTo("2017-05-15"), hasToString("{goodTweets=2, badTweets=0}")),
                        hasEntry(equalTo("2017-06-16"), hasToString("{goodTweets=0, badTweets=2}"))));

    }

    private Tweet tweet(String message, int month, int day) {
        return new Tweet(
                random.nextInt(),
                "a user id" + random.nextInt(),
                message,
                LocalDate.of(2017, month, day));
    }
}
