package com.zuhlke.ta.web;

import com.google.common.base.Strings;
import com.zuhlke.ta.prototype.*;
import com.zuhlke.ta.prototype.inmemory.InMemoryTweetService;
import com.zuhlke.ta.prototype.mapdb.MapDBTweetService;
import com.zuhlke.ta.sentiment.TwitterSentimentAnalyzerImpl;
import org.jetbrains.annotations.NotNull;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

public class Application {
    public static void main(String[] args) throws IOException {
        SentimentAnalyzer sentimentAnalyzer = new TwitterSentimentAnalyzerImpl();
        TweetService tweetService = new InMemoryTweetService(sentimentAnalyzer);
//        TweetService tweetService = new MapDBTweetService(sentimentAnalyzer);
        JobService jobService = new JobService(tweetService);
        Importer importer = new Importer(tweetService);
        importer.importTweetsFrom(new File("test_set_tweets.txt"));

        FreeMarkerEngine freeMarker = new FreeMarkerEngine();

//        staticFiles.location("/spark/template/freemarker");
        get("/", (req, resp) -> homepageData(jobService), freeMarker);
        get("/results/", (req, resp) -> jobService.getResults());
        get("/pending/", (req, resp) -> jobService.getPending());
        post("/jobs/", (req, resp) -> enqueueJob(jobService, req, resp));
    }

    @NotNull
    private static ModelAndView homepageData(JobService jobService) {
        Map<String, Object> model = new HashMap<>();
        model.put("results", jobService.getResults());
        model.put("pending", jobService.getPending());
        return new ModelAndView(model, "index.html");
    }

    @NotNull
    private static Object enqueueJob(JobService jobService, Request req, Response resp) {
        String keyword = req.queryMap("keyword").value();
        Query q = new Query(keyword);
        if (!Strings.isNullOrEmpty(keyword)) jobService.enqueueQuery(q);
        resp.redirect("/");
        return q;
    }
}
