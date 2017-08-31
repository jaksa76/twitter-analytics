package com.zuhlke.ta.prototype;

import java.io.Serializable;
import java.util.Date;

public class Tweet implements Serializable {
    public final long id;
    public final long userId;
    public final String message;
    public final Date date;

    public Tweet(long id, long userId, String message, Date date) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.date = date;
    }
}
