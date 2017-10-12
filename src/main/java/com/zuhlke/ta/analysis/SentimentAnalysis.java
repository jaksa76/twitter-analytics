package com.zuhlke.ta.analysis;

import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableRow;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.sentiment.SentimentAnalyzerImpl;
import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class SentimentAnalysis {
    private Properties props;
    private SentimentAnalyzer analyzer = new SentimentAnalyzerImpl();
    private TableReference srcTable;
    private TableReference destTable;
    private DataflowPipelineOptions pipelineOptions;

    public SentimentAnalysis() throws IOException {
        props = new Properties();
        props.load(Worker.class.getClassLoader().getResourceAsStream("configuration/bigquery.properties"));
        srcTable = new TableReference();
        srcTable.setDatasetId(props.getProperty("inputTweetsDataset"));
        srcTable.setTableId(props.getProperty("inputTweetsTable"));
        destTable = new TableReference();
        destTable.setDatasetId(props.getProperty("analysedTweetsDataset"));
        destTable.setTableId(props.getProperty("analysedTweetsTable"));
        pipelineOptions = getOptions();
    }

    public static void main(String[] args) throws IOException {
        new SentimentAnalysis().analyseAllTweets();
    }

    public void analyseAllTweets() {
        Pipeline pipeline = Pipeline.create(pipelineOptions);
        pipeline.apply(BigQueryIO.read().from(srcTable))
                .apply(ParDo.of(new DoFn<TableRow, TableRow>() {
                    @ProcessElement
                    public void processElement(ProcessContext ctx) {
                        TableRow input = ctx.element();
                        ctx.output(new TableRow()
                                .set("timestamp", input.get("timestamp"))
                                .set("content", input.get("content"))
                                .set("sentiment", analyzer.getSentiment((String) input.get("content"))));
                    }
                }))
                .apply(BigQueryIO.writeTableRows().to(destTable));

        pipeline.run().waitUntilFinish();
    }

    private DataflowPipelineOptions getOptions() throws IOException {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setProject(props.getProperty("projectId"));
        options.setRunner(DataflowRunner.class);
        options.setMaxNumWorkers(3);
        options.setGcpCredential(getCredentials());
        options.setZone("europe-west1");
        options.setTempLocation("gs://tweetanalyser-temp/dataflow");
        return options;
    }

    private GoogleCredentials getCredentials() throws IOException {
        File credentialsPath = new File(props.getProperty("serviceAccountCredFile"));
        try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
            return ServiceAccountCredentials.fromStream(serviceAccountStream);
        } catch (FileNotFoundException e) {
            return GoogleCredentials.getApplicationDefault();
        }
    }
}
