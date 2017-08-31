package com.zuhlke.ta.prototype;

import java.io.Serializable;
import java.util.Date;

public class Tweet implements Serializable {
    public final long id;
    public final String userId;
    public final String message;
    public final Date date;

    public Tweet(long id, String userId, String message, Date date) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.date = date;
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
}
