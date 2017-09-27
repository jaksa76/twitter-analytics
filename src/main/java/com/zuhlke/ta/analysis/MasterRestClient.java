package com.zuhlke.ta.analysis;

import spark.Spark;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class MasterRestClient implements MasterClient {
    @Override
    public void registerPartitionHandler(Callable<Object> callback) {
        Spark.get("/nextPartitionId", (req, resp) -> callback.call());
    }

    @Override
    public void registerStatusHandler(Callable<Object> callback) {
        Spark.get("/status", (req, resp) -> callback.call());
    }
}
