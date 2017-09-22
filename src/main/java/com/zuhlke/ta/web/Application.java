package com.zuhlke.ta.web;

import com.google.common.base.Strings;
import com.zuhlke.ta.athena.AthenaJdbcClient;
import com.zuhlke.ta.athena.AthenaTweetService;
import com.zuhlke.ta.model.Query;
import com.zuhlke.ta.service.JobService;
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
        AthenaJdbcClient athenaJdbcClient = new AthenaJdbcClient();
        AthenaTweetService tweetService = new AthenaTweetService(athenaJdbcClient);
        JobService jobService = new JobService(tweetService);

        FreeMarkerEngine freeMarker = new FreeMarkerEngine();

        get("/", (req, resp) -> homepageData(jobService), freeMarker);
        get("/results/", (req, resp) -> jobService.getResults());
        get("/pending/", (req, resp) -> jobService.getPending());
        post("/jobs/", (req, resp) -> enqueueJob(jobService, req, resp));
    }

    private static ModelAndView homepageData(JobService jobService) {
        Map<String, Object> model = new HashMap<>();
        model.put("results", jobService.getResults());
        model.put("pending", jobService.getPending());
        return new ModelAndView(model, "index.html");
    }

    private static Object enqueueJob(JobService jobService, Request req, Response resp) {
        System.out.println("enqueueJob");
        String keyword = req.queryMap("keyword").value();
        Query q = new Query(keyword);
        if (!Strings.isNullOrEmpty(keyword)) jobService.enqueueQuery(q);
        resp.redirect("/");
        return q;
    }
}
