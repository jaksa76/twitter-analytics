package com.zuhlke.ta.analysis;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import com.google.common.base.Stopwatch;
import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.sentiment.SentimentAnalyzerImpl;

import javax.ws.rs.client.ClientBuilder;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Worker {
    private BigQuery bigQuery;
    private SentimentAnalyzer analyzer;
    private Properties props;

    public static void main(String[] args) throws Exception {
        Worker worker = new Worker();
        worker.work();
    }

    public Worker() throws IOException {
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

    public void analyse(int partitionId, String srcDataset, String srcTable, String destDataset, String destTable) throws TimeoutException, InterruptedException, IOException {
        QueryJobConfiguration queryConfig = QueryJobConfiguration
                .newBuilder("SELECT content, timestamp" +
                        "          FROM " + srcDataset + "." + srcTable +
                        "         WHERE MOD(tweetId, 1000) = " + partitionId
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
                }
                InsertAllResponse insertAllResponse = bigQuery.insertAll(InsertAllRequest.of(TableId.of(destDataset, destTable), rows));

                if (insertAllResponse.hasErrors()) {
                    System.out.println(insertAllResponse.getInsertErrors());
                }

                result = result.getNextPage();
            }

            stopwatch.stop();
            System.out.println("done (" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms)");
        }
    }

    private void work() throws Exception {
        // TODO: get partition ID(s) from master and run them through the analyse method
        int partition = getPartition();
        while (partition != -1) {
            analyze(partition);
            partition = getPartition();
        }
    }

    private void analyze(int partition) throws TimeoutException, InterruptedException, IOException {
        analyse(partition, props.getProperty("inputTweetsDataset"), props.getProperty("inputTweetsTable"), props.getProperty("analysedTweetsDataset"), props.getProperty("analysedTweetsTable"));
    }

    public int getPartition() {
        String masterUrl = "http://localhost:4567/partition";
        return Integer.parseInt(ClientBuilder.newClient().target(masterUrl).request().get(String.class));
    }
}
