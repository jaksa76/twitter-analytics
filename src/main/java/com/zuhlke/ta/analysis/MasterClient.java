package com.zuhlke.ta.analysis;

import java.util.concurrent.Callable;

public interface MasterClient {
    void registerPartitionHandler(Callable<Object> callback);

    void registerStatusHandler(Callable<Object> callback);
}
