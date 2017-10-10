package com.zuhlke.ta.analysis;

import spark.Spark;

import static spark.Spark.get;

public class Master {
    private int nextPartitionId = 0;

    public static void main(String[] args) {
        Master master = new Master();

        get("/partition", (req, resp) -> master.getPartition());
    }

    private synchronized Integer getPartition() {
        if (nextPartitionId == 1000) return -1;
        return nextPartitionId++;
    }
}
