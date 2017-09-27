package com.zuhlke.ta.analysis;

import spark.Spark;

public class Master {
    private int nextPartitionId = 0;
    private MasterClient client;

    public static void main(String[] args) {
        Master master = new Master();
    }

    Master() {
        this.client = new MasterRestClient();
        this.client.registerPartitionHandler(this::getPartition);
        this.client.registerStatusHandler(this::getStatus);
    }

    private String getStatus() {
        return "OK";
    }

    private synchronized Integer getPartition() {
        if (nextPartitionId == 1000) return -1;
        return nextPartitionId++;
    }
}
