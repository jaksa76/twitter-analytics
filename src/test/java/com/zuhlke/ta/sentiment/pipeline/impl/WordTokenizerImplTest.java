package com.zuhlke.ta.sentiment.pipeline.impl;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class WordTokenizerImplTest {

    private final WordTokenizerImpl wordTokenizer = new WordTokenizerImpl();

    @Test
    public void removesEmailsFromText() {
        String text = "This is a tweet with zuh@lke.com email";
        List<String> tokenized = wordTokenizer.tokenize(text);
        assertThat(String.join(" ", tokenized), is(equalTo("This is a tweet with email")));
    }

    @Test
    public void removesNumbersFromText() {
        String text = "This is a tweet with 1234 number";
        List<String> tokenized = wordTokenizer.tokenize(text);
        assertThat(String.join(" ", tokenized), is(equalTo("This is a tweet with number")));
    }

    @Test
    public void removesURLsFromText() {
        String text = "This is a tweet with http://host.com:8080 url";
        List<String> tokenized = wordTokenizer.tokenize(text);
        assertThat(String.join(" ", tokenized), is(equalTo("This is a tweet with url")));
    }
}