package com.zuhlke.ta.dataflow;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.cloud.dataflow.sdk.Pipeline;
import com.google.cloud.dataflow.sdk.io.BigQueryIO;
import com.google.cloud.dataflow.sdk.options.BigQueryOptions;
import com.google.cloud.dataflow.sdk.options.PipelineOptionsFactory;
import com.google.cloud.dataflow.sdk.runners.BlockingDataflowPipelineRunner;
import com.google.cloud.dataflow.sdk.transforms.DoFn;
import com.google.cloud.dataflow.sdk.transforms.ParDo;
import com.google.cloud.dataflow.sdk.values.PCollection;
import com.zuhlke.ta.prototype.SentimentAnalyzer;
import com.zuhlke.ta.sentiment.SentimentAnalyzerImpl;

import java.util.ArrayList;
import java.util.List;

import static com.google.cloud.dataflow.sdk.io.BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED;
import static com.google.cloud.dataflow.sdk.io.BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE;

public class DataFlowSentimentAnalysis {
    private static final String BQ_INPUT_TABLE_NAME = "apt-sentinel-180609:intalert.test";
    private static final String BQ_OUTPUT_TABLE_NAME = "apt-sentinel-180609:intalert.analysed";
    private static final String GS_TEMP_LOCATION = "gs://mari-dataflow-staging";
    private static final String PROJECT_ID = "apt-sentinel-180609";
    private static SentimentAnalyzer analyzer = new SentimentAnalyzerImpl();

    public static void main(String[] args) {
        BigQueryOptions options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .create()
                .as(BigQueryOptions.class);
        options.setProject(PROJECT_ID);
        options.setTempLocation(GS_TEMP_LOCATION);
        options.setRunner(BlockingDataflowPipelineRunner.class);

        Pipeline pipeline = Pipeline.create(options);

        PCollection<TableRow> tweetData = pipeline.apply(
                BigQueryIO.Read
                        .named("ReadTweets")
                        .from(BQ_INPUT_TABLE_NAME));

        // define the output table schema
        TableSchema schema = new TableSchema();
        List<TableFieldSchema> fields = new ArrayList<>();
        fields.add(new TableFieldSchema().setName("timestamp").setType("TIMESTAMP"));
        fields.add(new TableFieldSchema().setName("content").setType("STRING"));
        fields.add(new TableFieldSchema().setName("sentiment").setType("FLOAT"));
        schema.setFields(fields);

        tweetData.apply(ParDo.named("AnalyseSentiment").of(new DoFn<TableRow, TableRow>() {
            @Override
            public void processElement(ProcessContext c) throws Exception {
                TableRow rowToInsert = new TableRow();
                String content = (String) c.element().get("content");
                String timestamp = (String) c.element().get("timestamp");
                double sentiment = analyzer.getSentiment(content);
                rowToInsert.set("timestamp", timestamp);
                rowToInsert.set("content", content);
                rowToInsert.set("sentiment", sentiment);
                c.output(rowToInsert);
            }
        }))
        .apply(BigQueryIO.Write.named("WriteOutput")
            .to(BQ_OUTPUT_TABLE_NAME)
            .withSchema(schema)
            .withCreateDisposition(CREATE_IF_NEEDED)
            .withWriteDisposition(WRITE_TRUNCATE)
        );

        pipeline.run();
    }
}
