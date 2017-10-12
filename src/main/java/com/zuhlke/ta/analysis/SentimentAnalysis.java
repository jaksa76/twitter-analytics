package com.zuhlke.ta.analysis;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.sentiment.SentimentAnalyzerImpl;
import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryOptions;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

public class SentimentAnalysis {
    private Properties props;
    private SentimentAnalyzer analyzer = new SentimentAnalyzerImpl();
    private TableReference srcTable;
    private TableReference destTable;
    private PipelineOptions pipelineOptions;

    public SentimentAnalysis() throws IOException {
        props = new Properties();
        props.load(Worker.class.getClassLoader().getResourceAsStream("configuration/bigquery.properties"));
        srcTable = new TableReference();
        srcTable.setProjectId(props.getProperty("projectId"));
        srcTable.setDatasetId(props.getProperty("inputTweetsDataset"));
        srcTable.setTableId(props.getProperty("inputTweetsTable"));
        destTable = new TableReference();
        destTable.setProjectId(props.getProperty("projectId"));
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
                                .set("sentiment", new SentimentAnalyzerImpl().getSentiment((String) input.get("content"))));
                    }
                }))
                .apply(BigQueryIO.writeTableRows().to(destTable).withSchema(schema(asList(
                        field("timestamp", "STRING"),
                        field("content", "STRING"),
                        field("sentiment", "FLOAT")
                ))));

        pipeline.run().waitUntilFinish();
    }

    private TableSchema schema(List<TableFieldSchema> fields) {
        return new TableSchema().setFields(fields);
    }

    private TableFieldSchema field(String name, String type) {
        return new TableFieldSchema().setName(name).setType(type);
    }

    private PipelineOptions getOptions() throws IOException {
        BigQueryOptions options = PipelineOptionsFactory.as(BigQueryOptions.class);
        options.setProject(props.getProperty("projectId"));
        options.setRunner(DataflowRunner.class);
        options.setGcpCredential(getCredentials());
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
