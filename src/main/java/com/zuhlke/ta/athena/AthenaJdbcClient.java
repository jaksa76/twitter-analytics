package com.zuhlke.ta.athena;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.sql.DriverManager.getConnection;

public class AthenaJdbcClient {

    private static final String athenaUrl = "jdbc:awsathena://athena.eu-west-1.amazonaws.com:443";

    @NotNull
    private static Properties properties() throws ClassNotFoundException {
        Class.forName("com.amazonaws.athena.jdbc.AthenaDriver");
        Properties info = new Properties();
        info.put("s3_staging_dir", "s3://intalert-backup/csv/query_results");
        info.put("log_path", "athenajdbc.log");
        info.put("aws_credentials_provider_class", "com.amazonaws.auth.PropertiesFileCredentialsProvider");
        info.put("aws_credentials_provider_arguments", "athenaCredentials.properties");
        return info;
    }

    public List<NeoTweet> selectContentMatching(String keyword) {
        System.out.println("selectContentMatching -> " + keyword);
        Connection conn = null;
        Statement statement = null;
        List<NeoTweet> results = new ArrayList<>();
        try {
            Properties info = properties();

            System.out.println("Connecting to Athena...");
            conn = getConnection(athenaUrl, info);

            long timeMillis = System.currentTimeMillis();
            statement = conn.createStatement();

            ResultSet rs = statement.executeQuery(
                "WITH matching_tweets (tdate, positive, negative) AS" +
                    " ( SELECT date(from_iso8601_timestamp(tstamp)) AS tdate," +
                    "         CASE when sentiment > 0.0 THEN '' ELSE null END AS positive," +
                    "         CASE when sentiment < 0.0 THEN '' ELSE null END AS negative" +
                    "  FROM intalert.tweets_partial" +
                    "  WHERE lower(content) LIKE '%"+keyword+"%')" +
                    " SELECT tdate," +
                    "       count(positive) AS positive_count," +
                    "       count(negative) AS negative_count" +
                    " FROM matching_tweets" +
                    " GROUP BY tdate" +
                    " ORDER BY tdate"
            );

            while (rs.next()) {
                results.add(NeoTweet.from(
                    rs.getString("tdate"),
                    rs.getString("positive_count"),
                    rs.getString("negative_count")));
            }

            System.out.println("fetched: " + results.size() + " and took: "
                + ((System.currentTimeMillis() - timeMillis)) + "ms");

            rs.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (Exception ex) {

            }
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }
        System.out.printf("Finished connectivity test.");
        return results;
    }

}
