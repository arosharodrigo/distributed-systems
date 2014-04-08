package main.java.server;

import main.java.domain.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 3/30/14
 * Time: 9:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class TCPServer extends AbstractServer {


    private static int SERVER_PORT = 7000;
    private static String SEPARATOR = " ";
    private static boolean READY_TO_STOP = true;

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

        TCPServer tcpServer = new TCPServer();
        ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
        ByteBuffer sendBuffer = ByteBuffer.allocate(1024);

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(SERVER_PORT));
        ssc.configureBlocking(false);

        READY_TO_STOP = false;
        while(!READY_TO_STOP) {
            try {
                System.out.println("Waiting for connections.....");
                SocketChannel sc = ssc.accept();

                if(sc == null) {
                    Thread.sleep(1000);
                } else {
                    sc.read(receiveBuffer);
                    Message req = (Message)Message.deserialize(receiveBuffer.array());
                    SocketAddress remoteAddress = sc.getRemoteAddress();
                    System.out.println("Received: [" + req.getMessage() + "] from: [" + remoteAddress + "]");
                    receiveBuffer.clear();
                    Arrays.fill(receiveBuffer.array(), (byte) 0);

                    String res = tcpServer.executeCommand(req.getMessage().split(SEPARATOR));
                    tcpServer.sendToClient(sendBuffer, res, sc, remoteAddress);
                }
            } catch (Exception e) {
                System.out.println("Error occurred while processing message: " + e.getMessage());
                e.printStackTrace();
            }
        }
        ssc.close();
    }

    private void sendToClient(ByteBuffer sendBuffer, String resp, SocketChannel sc, SocketAddress remoteAddress) throws IOException {
        Message res = new Message(resp);
        sendBuffer.put(Message.serialize(res));
        sendBuffer.rewind();
        sc.write(sendBuffer);
        sendBuffer.clear();
        Arrays.fill(sendBuffer.array(), (byte) 0);
    }

    @Override
    public void close() throws Exception {

    }
}
