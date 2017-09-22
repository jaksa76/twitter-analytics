package com.zuhlke.ta.web;

import com.google.common.base.Strings;
import com.zuhlke.ta.prototype.JobService;
import com.zuhlke.ta.prototype.Query;
import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.prototype.TweetService;
import com.zuhlke.ta.prototype.solutions.TweetServices;
import com.zuhlke.ta.sentiment.TwitterSentimentAnalyzerImpl;
import com.zuhlke.ta.sentiment.pipeline.impl.SentimentWordFinderImpl;
import com.zuhlke.ta.sentiment.utils.SentenceDetector;
import org.jetbrains.annotations.NotNull;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

public class Application {
    public static void main(String[] args) throws IOException, URISyntaxException {
        SentimentAnalyzer sentimentAnalyzer = TwitterSentimentAnalyzerImpl.create(SentenceDetector.fromResource(), SentimentWordFinderImpl.fromDictionaries());
//        TweetService tweetService = new InMemoryTweetService(sentimentAnalyzer);
//        TweetService tweetService = new MapDBTweetService(sentimentAnalyzer);
        TweetService tweetService = TweetServices.bigQuery(sentimentAnalyzer);
        JobService jobService = new JobService(tweetService);
//        Importer importer = new Importer(tweetService);
//        importer.importTweetsFrom(new File("test_set_tweets.txt"));

        FreeMarkerEngine freeMarker = new FreeMarkerEngine();

        get("/", (req, resp) -> homepageData(jobService), freeMarker);
        get("/results/", (req, resp) -> jobService.getResults());
        get("/pending/", (req, resp) -> jobService.getPending());
        post("/jobs/", (req, resp) -> enqueueJob(jobService, req, resp));

        System.out.println("Browse at http://localhost:4567/");

//        TwitterClientRunner.runClient(tweetService);
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
