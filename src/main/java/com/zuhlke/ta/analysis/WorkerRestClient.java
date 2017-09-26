package com.zuhlke.ta.analysis;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WorkerRestClient implements WorkerClient {
    private HttpClient client;
    private final String masterUrl = "http://localhost:4567";

    WorkerRestClient() {
        this.client = new DefaultHttpClient();
    }

    @Override
    public Integer getNextPartitionId() {
        System.out.print("Getting next partition ID... ");
        String url = masterUrl + "/nextPartitionId";
        HttpGet request = new HttpGet(url);
        Integer partitionId = -1;

        try {
            HttpResponse response = this.client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                System.out.println(result.toString());

                partitionId = Integer.parseInt(result.toString());
            } else {
                System.out.println("Failed to get partition ID, exiting...");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Failed to get partition ID, exiting...");
        }

        return partitionId;
    }
}
