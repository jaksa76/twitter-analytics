package com.zuhlke.ta.athena;

import com.zuhlke.ta.prototype.Tweet;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static java.sql.DriverManager.getConnection;

public class AthenaJdbcClient {

    private static final String athenaUrl = "jdbc:awsathena://athena.eu-west-1.amazonaws.com:443";

//    public static void main(String[] args) {
//        AthenaJdbcClient athenaJdbcClient = new AthenaJdbcClient();
//        List<Map> results = athenaJdbcClient.selectContentMatching("lagos");
//        results.forEach(System.out::println);
//    }

    @NotNull
    private static Properties properties() throws ClassNotFoundException {
        Class.forName("com.amazonaws.athena.jdbc.AthenaDriver");
        Properties info = new Properties();
        info.put("s3_staging_dir", "s3://intalert-backup/csv/tweets_partial_dedup");
        info.put("log_path", "athenajdbc.log");
        info.put("aws_credentials_provider_class", "com.amazonaws.auth.PropertiesFileCredentialsProvider");
        info.put("aws_credentials_provider_arguments", "athenaCredentials.properties");
        return info;
    }

    public Stream<Tweet> selectContentMatching(String keyword) {
        System.out.println("selectContentMatching -> " + keyword);
        Connection conn = null;
        Statement statement = null;
        List<Tweet> results = new ArrayList<>();
        try {
            Properties info = properties();

            System.out.println("Connecting to Athena...");
            conn = getConnection(athenaUrl, info);

            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(
                "select tweetid, usr, content, tstamp" +
                " from intalert.tweets_partial" +
                " where content" +
                " like '%?%'" +
                " limit 1000"
            );

            while (rs.next()) {
                String tweetid = rs.getString("tweetid");
                Tweet tweet = new Tweet(Long.parseLong(tweetid == null ? "-1" : tweetid),
                    rs.getString("usr"),
                    rs.getString("content"),
                    LocalDate.parse(rs.getString("tstamp").substring(0, 10))
                );
                results.add(tweet);
            }
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
        return results.stream();
    }

}
