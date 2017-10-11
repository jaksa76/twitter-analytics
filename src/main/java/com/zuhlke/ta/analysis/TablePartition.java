package com.zuhlke.ta.analysis;

import java.io.Serializable;

public class TablePartition implements Serializable {
    private final int partition;

    public TablePartition(int partition) {
        this.partition = partition;
    }

    public int getPartition() {
        return partition;
    }
}
