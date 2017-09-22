package com.zuhlke.ta.model;

public class Query {
    public final String keyword;

    public Query(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    @Override
    public String toString() {
        return "Query{" +
                "keyword='" + keyword + '\'' +
                '}';
    }
}
