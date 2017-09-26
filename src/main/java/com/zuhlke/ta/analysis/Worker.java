package com.zuhlke.ta.analysis;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import com.google.common.collect.Maps;
import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.sentiment.SentimentAnalyzerImpl;
import org.apache.commons.lang3.text.StrBuilder;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static jdk.nashorn.internal.codegen.OptimisticTypesPersistence.load;

public class Worker {
    private BigQuery bigQuery;
    private SentimentAnalyzer analyzer = new SentimentAnalyzerImpl();

    public static void main(String[] args) throws IOException {
        Worker worker = new Worker();
        worker.work();
    }

    private void work() {
        // get partitions from master and analyze them
    }


    public Worker() throws IOException {
        File credentialsPath = new File("service-account.json");
        try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
            this.bigQuery = BigQueryOptions.newBuilder().setProjectId("tweetanalyser-178311").setCredentials(credentials).build().getService();
        }
    }

    public void analyse(int partitionId, String src, String dest) throws TimeoutException, InterruptedException, IOException {
        QueryJobConfiguration queryConfig = QueryJobConfiguration
                .newBuilder("SELECT content, tstamp" +
                        "          FROM " + src +
                        "         WHERE MOD(tweetid, 1000) = " + partitionId +
                        "         LIMIT 10000"
                )
                .setUseLegacySql(false)
                .build();

        JobId jobId = JobId.of(UUID.randomUUID().toString());
        Job job = this.bigQuery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

        job = job.waitFor();

        if (job != null) {
            QueryResponse response = this.bigQuery.getQueryResults(jobId);
            QueryResult result = response.getResult();
            while (result != null) {
                List<InsertAllRequest.RowToInsert> rows = new ArrayList<>();
                for (List<FieldValue> row : result.iterateAll()) {
                    String content = row.get(0).getStringValue();
                    String timestamp = row.get(1).getStringValue();
                    double sentiment = analyzer.getSentiment(content);

                    Map<String, Object> fields = new HashMap<>();
                    fields.put("content", content);
                    fields.put("tstamp", timestamp);
                    fields.put("sentiment", sentiment);
                    rows.add(InsertAllRequest.RowToInsert.of(fields));
                }
                InsertAllResponse insertAllResponse = bigQuery.insertAll(InsertAllRequest.of(TableId.of("Tweets", "analyzed"), rows));
                System.out.println(insertAllResponse.getInsertErrors());

                result = result.getNextPage();
            }
        }
    }

}
