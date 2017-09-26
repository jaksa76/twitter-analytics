package com.zuhlke.ta.analysis;

public class MasterClient {
    public void registerPartitionHandler(Runnable function) {
        function.run();
    }

    public void registerStatusHandler(Runnable function) {
        function.run();
    }
}
