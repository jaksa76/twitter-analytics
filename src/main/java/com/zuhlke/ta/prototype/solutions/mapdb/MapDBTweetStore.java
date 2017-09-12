package com.zuhlke.ta.prototype.solutions.mapdb;

import com.zuhlke.ta.prototype.Tweet;
import com.zuhlke.ta.prototype.solutions.common.TweetStore;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public class MapDBTweetStore implements TweetStore, Closeable {
    public static final String TWEETS_DB = "tweets.db";
    private final DB db;
    private final HTreeMap<Object, Tweet> tweets;

    @SuppressWarnings("unchecked")
    public MapDBTweetStore() {
        db = DBMaker.fileDB(new File(MapDBTweetStore.TWEETS_DB)).make();
        tweets = (HTreeMap<Object, Tweet>) db.hashMap("tweets").createOrOpen();
    }

    @Override
    public void importTweets(Stream<Tweet> tweetStream) {
        tweetStream.forEach(t -> tweets.put(t.id, t));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<Tweet> tweets() {
        return tweets.values().stream();
    }

    @Override
    public void close() throws IOException {
        db.close();
    }
}
