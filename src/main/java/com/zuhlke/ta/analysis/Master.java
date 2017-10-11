package com.zuhlke.ta.analysis;

import org.jgroups.*;

public class Master extends ReceiverAdapter implements ChannelListener {
    private JChannel channel;
    private int nextPartitionId = 0;

    public Master() throws Exception {
        this.channel = new JChannel("jgroups.xml");
        this.channel.setReceiver(this).addChannelListener(this);
        channel.connect("analysis");
    }

    public static void main(String[] args) throws Exception {
        Master master = new Master();
    }

    public void receive(Message msg) {
        if (msg.getObject() instanceof PartitionRequest) {
            System.out.println("got a partition request from " + msg.getSrc());
            try {
                channel.send(new Message(null, new TablePartition(getPartition())));
                System.out.println("partition sent");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private synchronized Integer getPartition() {
        if (nextPartitionId == 10000) return -1;
        return nextPartitionId++;
    }

    @Override
    public void channelConnected(JChannel channel) {
        System.out.println("Master.channelConnected");
    }

    @Override
    public void channelDisconnected(JChannel channel) {
        System.out.println("Master.channelDisconnected");
    }

    @Override
    public void channelClosed(JChannel channel) {
        System.out.println("Master.channelClosed");
    }
}
