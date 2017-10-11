package com.zuhlke.ta.analysis;

import net.didion.jwnl.data.Exc;
import org.jgroups.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by jvu on 10/11/2017.
 */
public class Chat extends ReceiverAdapter implements ChannelListener {
    JChannel channel;
    String username = System.getProperty("user.name", "n/a");

    private void start() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this).addChannelListener(this);
        channel.connect("ChatCluster");
        eventLoop();
        channel.close();
    }

    private void eventLoop() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print("> ");
                System.out.flush();
                String line = in.readLine().toLowerCase();
                if (line.startsWith("quit") || line.startsWith("exit")) break;
                line = "[" + username + "] " + line;
                channel.send(new Message(null, line));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Chat().start();
    }

    @Override
    public void viewAccepted(View view) {
        System.out.println("** view: " + view);
    }

    @Override
    public void receive(Message msg) {
        System.out.println(msg.getSrc() + ": " + msg.getObject());
    }

    @Override
    public void channelConnected(JChannel channel) {
        System.out.println("Chat.channelConnected");
    }

    @Override
    public void channelDisconnected(JChannel channel) {
        System.out.println("Chat.channelDisconnected");
    }

    @Override
    public void channelClosed(JChannel channel) {
        System.out.println("Chat.channelClosed");
    }
}
