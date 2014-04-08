package main.java.server;

import main.java.domain.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
public class UDPServer extends AbstractServer {

    private static int SERVER_PORT = 7000;
    private static String SEPARATOR = " ";
    private static boolean READY_TO_STOP = true;
    private static DatagramChannel CHANNEL;

    public UDPServer() {
        super();
    }

    public static void main(String[] args) throws Exception {
        UDPServer udpServer = new UDPServer();

        ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
        ByteBuffer sendBuffer = ByteBuffer.allocate(1024);

        CHANNEL = DatagramChannel.open();
        CHANNEL.socket().bind(new InetSocketAddress(SERVER_PORT));
        READY_TO_STOP = false;

        while (!READY_TO_STOP) {
            try {
                System.out.println("Waiting for connections.....");
                SocketAddress remoteAddress = CHANNEL.receive(receiveBuffer);
                Message req = (Message)Message.deserialize(receiveBuffer.array());
                System.out.println("Received: [" + req.getMessage() + "] from: [" + remoteAddress + "]");
                receiveBuffer.clear();
                Arrays.fill(receiveBuffer.array(), (byte) 0);

                String res = udpServer.executeCommand(req.getMessage().split(SEPARATOR));
                udpServer.sendToClient(sendBuffer, res, remoteAddress);

            } catch (Exception e) {
                System.out.println("Error occurred while processing message: " + e.getMessage());
                e.printStackTrace();
            }
        }

        udpServer.close();
    }

    private void sendToClient(ByteBuffer sendBuffer, String resp, SocketAddress remoteAddress) throws IOException {
        Message res = new Message(resp);
        sendBuffer.put(Message.serialize(res));
        sendBuffer.flip();
        CHANNEL.send(sendBuffer, remoteAddress);
        sendBuffer.clear();
        Arrays.fill(sendBuffer.array(), (byte) 0);
    }

    @Override
    public void close() throws Exception {

    }

}
