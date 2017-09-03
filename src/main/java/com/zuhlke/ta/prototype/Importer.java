package com.zuhlke.ta.prototype;

import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class Importer {
    private static final int MAX_TWEETS_TO_IMPORT = 100000;

    private TweetService tweetService;

    public Importer(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    public void importTweetsFrom(File file) throws IOException {
        final AtomicLong count = new AtomicLong();
        tweetService.importTweets(
            Files.lines(file.toPath())
                    .map(line -> line.split("\\|"))
                    .filter(parts -> parts.length == 12)
                    .map(Importer::asTweet)
                    .filter(Optional::isPresent)
                    .limit(MAX_TWEETS_TO_IMPORT)
                    .map(Optional::get)
                    .peek(t -> count.incrementAndGet()));

        System.out.println("imported " + count.longValue() + " tweets");
    }

    private static Optional<Tweet> asTweet(String[] parts) {
        try {
            return Optional.of(new Tweet(getId(parts[1]), parts[3], parts[2], parseDate(parts[4])));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Could not parse number " + parts[1]);
        }
        return Optional.empty();
    }


    private static long getId(String part) {
        return Strings.isNullOrEmpty(part) ? 0L : Long.parseLong(part);
    }

    private static LocalDate parseDate(String part) throws ParseException {
        if (part.length() < 10) throw new ParseException("Could not parse date " + part, 0);
        try {
            return LocalDate.parse(part.substring(0, 10));
        } catch (DateTimeException e) {
            throw new ParseException("Could not parse date " + part + e.getMessage(), 0);
        }
    }
}
