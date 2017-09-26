package com.zuhlke.ta.analysis;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WorkerRestClient implements WorkerClient {
    private HttpClient client;
    private final String masterUrl = "http://localhost:4567";

    WorkerRestClient() {
        this.client = new DefaultHttpClient();
    }

    @Override
    public void connectToMaster() throws InterruptedException {
        boolean connected = false;
        String url = masterUrl + "/status";
        HttpGet request = new HttpGet(url);

        while(!connected) {
            try {
                System.out.print("Connecting to master at " + masterUrl + "... ");
                HttpResponse response = this.client.execute(request);
                if (response.getStatusLine().getStatusCode() == 200) {
                    // don't need the response at the moment, but read it to the end to make sure the
                    // connection is closed
                    readResponseAsString(response);
                    connected = true;
                    System.out.println("connected");
                }
            } catch (Exception ex) {
                System.out.println("failed, retrying in 10s");
                Thread.sleep(10 * 1000);
            }
        }
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
                String result = readResponseAsString(response);

                System.out.println(result);
                partitionId = Integer.parseInt(result);
            } else {
                System.out.println("failed to get partition ID, exiting...");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("failed to get partition ID, exiting...");
        }

        return partitionId;
    }

    private String readResponseAsString(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }
}
