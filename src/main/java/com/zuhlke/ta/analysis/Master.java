package com.zuhlke.ta.analysis;

import spark.Spark;

import java.io.IOException;

import static spark.Spark.port;

public class Master {
    private int nextPartitionId = 0;
    private MasterClient client;

    public static void main(String[] args) throws IOException {
        Master master = new Master();
    }

    Master() throws IOException {
        client = new MasterRestClient();
        ConfigurationLoader config = new ConfigurationLoader();
        Integer port = Integer.parseInt(config.getConfigItem("masterPort"));
        port(port);
        client.registerPartitionHandler(this::getPartition);
        client.registerStatusHandler(this::getStatus);

        System.out.println("Listening on port " + port + "...");
    }

    private String getStatus() {
        return "OK";
    }

    private synchronized Integer getPartition() {
        if (nextPartitionId == 1000) return -1;
        return nextPartitionId++;
    }
}
