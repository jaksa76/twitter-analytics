package com.zuhlke.ta.analysis;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jvu on 9/22/2017.
 */
public class WorkerTest {
    @Test
    public void analysePartition() throws Exception {
        new Worker().analyse(0, "Tweets.all", "Tweets.analysed");
    }
}