package com.zuhlke.ta.analysis;

import spark.Spark;

public class Master {
    private int nextPartitionId = 0;

    public static void main(String[] args) {
        Master master = new Master();

        // TODO: Provide a method for Workers to get the ID of the next partition that they should work on
    }

    private synchronized Integer getPartition() {
        if (nextPartitionId == 1000) return -1;
        return nextPartitionId++;
    }
}
