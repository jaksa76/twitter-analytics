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

import java.io.*;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

public class SentimentAnalysis implements Serializable {
    private static SentimentAnalyzer analyzer = new SentimentAnalyzerImpl();
    private Properties props;

    private SentimentAnalysis() throws IOException {
        props = new Properties();
        props.load(getClass().getClassLoader().getResourceAsStream("configuration/bigquery.properties"));
    }

    public static void main(String[] args) throws IOException {
        new SentimentAnalysis().analyseAllTweets();
    }

    private void analyseAllTweets() throws IOException {
        Pipeline pipeline = Pipeline.create(getOptions());
        pipeline.apply(BigQueryIO.read().from(srcTable(props)))
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
                .apply(BigQueryIO.writeTableRows().to(destTable(props)).withSchema(schema(asList(
                        field("timestamp", "STRING"),
                        field("content", "STRING"),
                        field("sentiment", "FLOAT")
                ))));

        pipeline.run().waitUntilFinish();
    }

    private TableReference srcTable(Properties props) {
        return table(props.getProperty("projectId"), props.getProperty("inputTweetsDataset"), props.getProperty("inputTweetsTable"));
    }


    private TableReference destTable(Properties props) {
        return table(props.getProperty("projectId"), props.getProperty("analysedTweetsDataset"), props.getProperty("outputTweetsTable"));
    }

    private TableReference table(String projectId, String datasetId, String tableId) {
        TableReference tableReference = new TableReference();
        tableReference.setProjectId(projectId);
        tableReference.setDatasetId(datasetId);
        tableReference.setTableId(tableId);
        return tableReference;
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
