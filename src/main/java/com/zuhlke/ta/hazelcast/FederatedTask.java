package com.zuhlke.ta.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.io.IOException;
import java.util.Date;

public class FederatedTask {

    private static ReservationManager reservationManager;
    private static String workerId;

    public static void main(String[] args) {
        Config cfg = new Config();
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
        workerId = instance.getLocalEndpoint().getUuid();
        reservationManager = new ReservationManager(instance, workerId);
        System.out.println("Worker " + workerId + " starting");
        try {
            workUntilNoMoreTasks();
        } catch (IOException e) {
            e.printStackTrace();
        }
        instance.shutdown();
        System.out.println("Worker " + workerId + " finishing");
    }

    private static void workUntilNoMoreTasks() throws IOException {
        while(true) {
            int partition = -1;
            int triesLeft = 3; // retry a few times in case of transaction collision
            while (partition < 0) {
                if (triesLeft-- <= 0) {
                    return;
                }
                partition = reservationManager.reserve();
            }
            performTask(partition);
        }
    }

    private static void performTask(int partition) {
        // Pretend to do some work
        final int chunkCount = partition % 4 + 1;
        Date expiryTime = reservationManager.getExpiryTime();
        for (int i = 0; i < chunkCount; i++) {
            System.out.println("Starting work on partition no. " + partition + ", chunk " + i);
            final long sleepTime = expiryTime.getTime() - System.currentTimeMillis();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {
            }
            expiryTime = reservationManager.refreshTimestamp(partition);
        }
        reservationManager.markCompleted(partition);
    }
}
