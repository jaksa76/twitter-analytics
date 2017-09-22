package com.zuhlke.ta.prototype;

import java.io.Serializable;
import java.time.LocalDate;

public class Tweet implements Serializable {
    public final long id;
    public final String userId;
    public final String message;
    public final LocalDate date;
    public final Float sentiment;

    public Tweet(long id, String userId, String message, LocalDate date, Float sentiment) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.date = date;
        this.sentiment = sentiment;
    }

    @Override
    public String toString() {
        return "Tweet{" +
            "id=" + id +
            ", userId='" + userId + '\'' +
            ", message='" + message + '\'' +
            ", date=" + date +
            '}';
    }

    public static Tweet from(String tweetId, String usr, String content, String timestamp, float sentiment) {
        long id = Long.parseLong(tweetId == null ? "-1" : tweetId);
        LocalDate date = LocalDate.parse(timestamp.substring(0, 10));
        return new Tweet(id, usr, content, date, sentiment);
    }
}
