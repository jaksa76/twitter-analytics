package com.zuhlke.ta.analysis;

import spark.Spark;

public class Master {
    private int nextPartitionId = 0;
    private MasterClient client;

    public static void main(String[] args) {
        Master master = new Master();
    }

    Master() {
        this.client = new MasterClient();
        this.client.registerPartitionHandler(() -> {
            Spark.get("/nextPartitionId", (req, resp) -> this.getPartition());
        });
        this.client.registerStatusHandler(() -> {
            Spark.get("/status", (req, resp) -> "OK");
        });
    }

    synchronized Integer getPartition() {
        if (nextPartitionId == 1000) return -1;
        return nextPartitionId++;
    }
}
