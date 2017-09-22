package com.zuhlke.ta.prototype.solutions.bigquery;

import com.google.cloud.bigquery.*;
import com.zuhlke.ta.prototype.Tweet;
import com.zuhlke.ta.prototype.solutions.common.TweetStore;
import com.zuhlke.ta.sentiment.pipeline.impl.FatalError;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class BigQueryTweetStore implements TweetStore {
    private final BigQuery bigQuery;

    public BigQueryTweetStore(BigQuery bigQuery) {
        this.bigQuery = bigQuery;
    }

    @Override
    public void importTweets(Stream<Tweet> tweets) {
        throw new FatalError("Shouldn't be doing this");
    }

    @Override
    public Stream<Tweet> tweets() {
        QueryResponse response = queryResponse();
        if (response.hasErrors()) {
            System.err.println(response.getExecutionErrors());
            return Stream.empty();
        }

        return StreamSupport.stream(lazySpliterator(response.getResult().iterateAll()), false)
                .map(BigQueryTweetStore::asTweet)
                .peek(System.out::println);
    }

    private static Spliterator<List<FieldValue>> lazySpliterator(final Iterable<List<FieldValue>> results) {
        final Iterator<List<FieldValue>> iterator = results.iterator();

        return new Spliterator<List<FieldValue>>() {
            @Override
            public boolean tryAdvance(Consumer<? super List<FieldValue>> action) {
                if (iterator.hasNext()) {
                    action.accept(iterator.next());
                    return true;
                }
                return false;
            }

            @Override
            public Spliterator<List<FieldValue>> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return -1;
            }

            @Override
            public int characteristics() {
                return ORDERED | DISTINCT | NONNULL | IMMUTABLE;
            }
        };
    }


    @NotNull
    private QueryResponse queryResponse() {
        try {
            QueryResponse response = bigQuery.query(QueryRequest.of("SELECT * FROM [zuhlke-camp:zuhlke_camp_dataset.tweets_with_keyword] ORDER BY timestamp ASC"));
            while (!response.jobCompleted()) {
                TimeUnit.MILLISECONDS.sleep(100);
                response = bigQuery.getQueryResults(response.getJobId());
            }
            return response;
        } catch (InterruptedException e) {
            throw new FatalError("interrupted", e);
        }
    }

    private static Tweet asTweet(List<FieldValue> fieldValues) {
        return new Tweet(123,
                fieldValues.get(3).getStringValue(),
                fieldValues.get(2).getStringValue(),
                dateFrom(fieldValues));
    }

    private static LocalDate dateFrom(List<FieldValue> fieldValues) {
        return Instant.ofEpochMilli(MILLISECONDS.convert(fieldValues.get(4).getTimestampValue(), MICROSECONDS))
                .atZone(ZoneId.of("UTC"))
                .toLocalDate();
    }

    public static BigQueryTweetStore create() {
        final BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();

        return new BigQueryTweetStore(bigQuery);
    }
}
