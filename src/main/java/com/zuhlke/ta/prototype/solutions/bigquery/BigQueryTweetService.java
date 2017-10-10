package com.zuhlke.ta.prototype.solutions.bigquery;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import com.zuhlke.ta.prototype.Query;
import com.zuhlke.ta.prototype.SentimentTimeline;
import com.zuhlke.ta.prototype.SentimentTimeline.Day;
import com.zuhlke.ta.prototype.Tweet;
import com.zuhlke.ta.prototype.TweetService;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class BigQueryTweetService implements TweetService {

    private final String inputTweetsDataset;
    private final String inputTweetsTable;
    private BigQuery bigQuery;
    private Properties props;

    public BigQueryTweetService() throws IOException {
        props = new Properties();
        props.load(BigQueryTweetService.class.getClassLoader().getResourceAsStream("configuration/bigquery.properties"));

        final String serviceAccountCredFile = props.getProperty("serviceAccountCredFile");
        //File credentialsPath = new File(serviceAccountCredFile);
        final InputStream serviceAccountStream = BigQueryTweetService.class.getClassLoader().getResourceAsStream(serviceAccountCredFile);
        try {
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
            this.bigQuery = BigQueryOptions.newBuilder().setProjectId(props.getProperty("projectId")).setCredentials(credentials).build().getService();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputTweetsDataset = this.props.getProperty("inputTweetsDataset");
        inputTweetsTable = this.props.getProperty("inputTweetsTable");
    }

    @Override
    public SentimentTimeline analyzeSentimentOverTime(Query q) {


        Map<String, Day> sentimentByDay = new HashMap<>();
        try {
            Map<String, Integer> goodTweets = executeQuery(true, q.keyword);
            Map<String, Integer> badTweets = executeQuery(false, q.keyword);
            sentimentByDay.putAll(MapUtils.merge(goodTweets, badTweets));
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        return new SentimentTimeline(q.keyword, sentimentByDay);
    }

    private Map<String, Integer> executeQuery(boolean positiveSentiment, String keyword) throws InterruptedException, TimeoutException {
        Map<String, Integer> sentimentByDay = new LinkedHashMap<>();
        QueryJobConfiguration queryConfig = QueryJobConfiguration
                .newBuilder("SELECT DATE(timestamp) AS date, COUNT(*) AS count\n" +
                        "FROM " + inputTweetsDataset + "." + inputTweetsTable + "\n" +
                        "WHERE LOWER(content) LIKE @keyword\n" +
                        "AND sentiment " + (positiveSentiment ? ">" : "<") + " 0.0\n" +
                        "GROUP BY date\n" +
                        "ORDER BY date")
                .setUseLegacySql(false)
                .addNamedParameter("keyword", QueryParameterValue.string("%" + keyword.toLowerCase() + "%"))
                .build();

        JobId jobId = JobId.of(UUID.randomUUID().toString());
        Job job = this.bigQuery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

        job = job.waitFor();

        if (job != null) {
            QueryResponse response = this.bigQuery.getQueryResults(jobId);
            QueryResult result = response.getResult();
            while (result != null) {
                for (List<FieldValue> row : result.iterateAll()) {
                    String date = row.get(0).getStringValue();
                    int tweets = (int) row.get(1).getLongValue();
                    sentimentByDay.put(date, tweets);
                }

                result = result.getNextPage();
            }
        }

        return sentimentByDay;
    }

    @Override
    public void importTweets(Collection<Tweet> tweets) {
        // already done
    }
}
