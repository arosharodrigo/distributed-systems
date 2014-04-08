package main.java.client;

import main.java.domain.Message;
import main.java.output.impl.ConsoleScreen;
import main.java.output.Screen;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 3/30/14
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class UDPClient extends AbstractClient {

    private static DatagramChannel CHANNEL;

    public static void main(String[] args) throws Exception {

        Screen screen = new ConsoleScreen();
        UDPClient udpClient = new UDPClient();

        System.out.println("###########################################################################################");
        System.out.println("#################################  UDP CLIENT STARTED #####################################");
        System.out.println("###########################################################################################");

        //Initial configuration settings with user inputs
        String configs = screen.print(
                "Please specify initial arguments in order " +
                        "[<client-port>,<server-ip>,<server-port>]\n" +
                        "If you need default configs[8000,127.0.0.1,7000] just skip by typing ENTER key:\n" +
                        ":>"
        );

        if(configs != null && !configs.isEmpty()) {
            udpClient.setConfigurations(configs.split(","));
        }

        //Channel creation
        CHANNEL = DatagramChannel.open();
        CHANNEL.socket().bind(new InetSocketAddress(CLIENT_PORT));

        ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
        ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
        //Notifying server initially
        udpClient.notifyServer(sendBuffer, receiveBuffer);

        while(true) {
            //sending
            String cmd = screen.print(":>");
            if (cmd.equals("bye")) {
                udpClient.sendToServer(cmd, sendBuffer);
                udpClient.close();
                System.exit(0);
            }
            udpClient.sendToServer(cmd, sendBuffer);

            //receiving
            CHANNEL.receive(receiveBuffer);
            Message res = (Message)Message.deserialize(receiveBuffer.array());
            udpClient.handleResponse(res);
            receiveBuffer.clear();
            Arrays.fill(receiveBuffer.array(), (byte) 0);
        }
    }

    private void notifyServer(ByteBuffer sendBuffer, ByteBuffer receiveBuffer) throws IOException {
        sendToServer("hello", sendBuffer);
        CHANNEL.receive(receiveBuffer);
        receiveBuffer.clear();
        Arrays.fill(receiveBuffer.array(), (byte) 0);
    }

    public void sendToServer(String cmd, ByteBuffer sendBuffer) throws IOException {
        Message req = new Message(cmd);
        sendBuffer.put(Message.serialize(req));
        sendBuffer.flip();
        CHANNEL.send(sendBuffer, new InetSocketAddress(SERVER_IP, SERVER_PORT));
        sendBuffer.clear();
        Arrays.fill(sendBuffer.array(), (byte) 0);
    }

    public void handleResponse(Message res) {
        System.out.println(res.getMessage());
    }

    public void close() throws Exception {
        CHANNEL.socket().close();
    }

}
