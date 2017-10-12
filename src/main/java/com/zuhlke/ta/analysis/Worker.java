package com.zuhlke.ta.analysis;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import com.google.common.base.Stopwatch;
import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.sentiment.SentimentAnalyzerImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Worker {
    private BigQuery bigQuery;
    private SentimentAnalyzer analyzer;
    private Properties props;

    public static void main(String[] args) throws Exception {
        Worker worker = new Worker();
    }

    public Worker() throws Exception {
        props = new Properties();
        props.load(Worker.class.getClassLoader().getResourceAsStream("configuration/bigquery.properties"));
        analyzer = new SentimentAnalyzerImpl();

        File credentialsPath = new File(props.getProperty("serviceAccountCredFile"));
        try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
            this.bigQuery = BigQueryOptions.newBuilder().setProjectId(props.getProperty("projectId")).setCredentials(credentials).build().getService();
        } catch (FileNotFoundException e) {
            Credentials credentials = GoogleCredentials.getApplicationDefault();
            this.bigQuery = BigQueryOptions.newBuilder().setProjectId(props.getProperty("projectId")).setCredentials(credentials).build().getService();
        }
    }

    void analyse(int partitionId, String srcDataset, String srcTable, String destDataset, String destTable) throws TimeoutException, InterruptedException, IOException {
        QueryJobConfiguration queryConfig = QueryJobConfiguration
                .newBuilder("SELECT content, timestamp" +
                        "          FROM " + srcDataset + "." + srcTable +
                        "         WHERE MOD(tweetId, 10000) = " + partitionId
                )
                .setUseLegacySql(false)
                .build();

        JobId jobId = JobId.of(UUID.randomUUID().toString());
        Job job = this.bigQuery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

        System.out.print("Retrieving tweets for partition... ");
        job = job.waitFor();

        if (job != null) {
            QueryResponse response = this.bigQuery.getQueryResults(jobId);
            QueryResult result = response.getResult();

            System.out.println(result.getTotalRows() + " tweets");
            System.out.print("Analysing... ");
            Stopwatch stopwatch = Stopwatch.createStarted();

            while (result != null) {
                List<InsertAllRequest.RowToInsert> rows = new ArrayList<>();
                for (List<FieldValue> row : result.iterateAll()) {
                    String content = row.get(0).getStringValue();
                    String timestamp = row.get(1).getStringValue();

                    double sentiment = analyzer.getSentiment(content);

                    Map<String, Object> fields = new HashMap<>();
                    fields.put("content", content);
                    fields.put("timestamp", timestamp);
                    fields.put("sentiment", sentiment);
                    rows.add(InsertAllRequest.RowToInsert.of(fields));

                    if (rows.size() == 100) {
                        insert(destDataset, destTable, rows);
                        rows.clear();
                    }

                }
                insert(destDataset, destTable, rows);

                result = result.getNextPage();
            }

            stopwatch.stop();
            System.out.println("done (" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms)");
        }
    }

    private void insert(String destDataset, String destTable, List<InsertAllRequest.RowToInsert> rows) {
        if (rows.isEmpty()) return;
        InsertAllResponse insertAllResponse = bigQuery.insertAll(InsertAllRequest.of(TableId.of(destDataset, destTable), rows));
        if (insertAllResponse.hasErrors()) {
            System.out.println(insertAllResponse.getInsertErrors());
        }
    }

    private void analyse(int partition) throws TimeoutException, InterruptedException, IOException {
        analyse(partition, props.getProperty("inputTweetsDataset"), props.getProperty("inputTweetsTable"), props.getProperty("analysedTweetsDataset"), props.getProperty("analysedTweetsTable"));
    }
}
