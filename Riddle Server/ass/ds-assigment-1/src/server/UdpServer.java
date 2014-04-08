package server;

import java.io.IOException;
import java.net.*;

public class UdpServer extends AbstractServer {
    private DatagramSocket socket;
    private final int PORT = 7500;
    private boolean readyToStop = true;

    public UdpServer() {
        super();
        new Thread(this).start();
    }

    public void run() {
        readyToStop= false;

        while (!readyToStop) {
            try {
                socket = new DatagramSocket(PORT);
                byte[] buf = new byte[2048];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                String resp = handleMessageFromClient(message);
                sendResponseToClient(resp, packet);
            } catch (IOException e) {
                System.out.println("Error occurred while receiving message:" + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    close();
                } catch (Exception e) {

                }
            }
        }
    }

    private void sendResponseToClient(String resp, DatagramPacket packet) throws IOException {
        if (null == resp) {
            return;
        }
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        byte[] buf = resp.getBytes();
        System.out.println("Sending message to ip[" + address + "] port[" + port + "]");
        packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
    }

    public void close() throws Exception {
        socket.close();
    }

    public static void main(String[] args) {
        UdpServer server = new UdpServer();
    }


}
