package main.java.client;

import main.java.domain.Message;
import main.java.output.Screen;
import main.java.output.impl.ConsoleScreen;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 3/31/14
 * Time: 2:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class TCPClient extends AbstractClient {

    private static SocketChannel CHANNEL;

    public static void main(String[] args) throws Exception {
        Screen screen = new ConsoleScreen();
        TCPClient tcpClient = new TCPClient();

        System.out.println("###########################################################################################");
        System.out.println("#################################  TCP CLIENT STARTED #####################################");
        System.out.println("###########################################################################################");

        //Initial configuration settings with user inputs
        String configs = screen.print(
                "Please specify initial arguments in order " +
                        "[<client-port>,<server-ip>,<server-port>]\n" +
                        "If you need default configs[8000,127.0.0.1,7000] just skip by typing ENTER key:\n" +
                        ":>"
        );

        if(configs != null && !configs.isEmpty()) {
            tcpClient.setConfigurations(configs.split(","));
        }

        CHANNEL = SocketChannel.open();
        CHANNEL.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

        ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
        ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
        //Notifying server initially
        tcpClient.notifyServer(sendBuffer, receiveBuffer);

        while(true) {
            //sending
            String cmd = screen.print(":>");
            if (cmd.equals("bye")) {
                tcpClient.sendToServer(cmd, sendBuffer);
                tcpClient.close();
                System.exit(0);
            }
            tcpClient.sendToServer(cmd, sendBuffer);

            //receiving
            CHANNEL.read(receiveBuffer);
            receiveBuffer.flip();
            Message res = (Message)Message.deserialize(receiveBuffer.array());
            tcpClient.handleResponse(res);
            receiveBuffer.clear();
            Arrays.fill(receiveBuffer.array(), (byte) 0);
        }
    }

    private void notifyServer(ByteBuffer sendBuffer, ByteBuffer receiveBuffer) throws IOException {
        sendToServer("hello", sendBuffer);
        CHANNEL.read(receiveBuffer);
        receiveBuffer.clear();
        Arrays.fill(receiveBuffer.array(), (byte) 0);
    }

    private void sendToServer(String cmd, ByteBuffer sendBuffer) throws IOException {
        Message req = new Message(cmd);
        sendBuffer.put(Message.serialize(req));
        sendBuffer.flip();
        while(sendBuffer.hasRemaining()) {
            CHANNEL.write(sendBuffer);
        }
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
