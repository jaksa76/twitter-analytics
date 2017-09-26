package com.zuhlke.ta.analysis;

import spark.Spark;

public class Master {
    private int nextPartitionId = 0;

    public static void main(String[] args) {
        Master master = new Master();

        Spark.get("/nextPartitionId", (req, resp) -> master.getPartition());
    }

    synchronized Integer getPartition() {
        if (nextPartitionId == 1000) return -1;
        return nextPartitionId++;
    }
}
