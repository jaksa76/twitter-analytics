package com.zuhlke.ta.analysis;

public interface WorkerClient {
    void connectToMaster() throws InterruptedException;
    Integer getNextPartitionId();
}
