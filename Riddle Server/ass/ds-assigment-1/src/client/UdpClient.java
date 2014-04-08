package client;

import java.net.*;

public class UdpClient implements AbstractClient {
    private String serverHost = "";
    private int serverPort = 7500;
    DatagramSocket socket;

    public UdpClient() throws SocketException {
        socket = new DatagramSocket();
    }

    public void sendToServer(String req) throws Exception {

        byte[] buf = req.getBytes();
        InetAddress address = InetAddress.getByName(serverHost);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, serverPort);
        socket.send(packet);

        buf = new byte[2048];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        String received = new String(packet.getData(), 0, packet.getLength());
        handleResponse(received);
    }

    public void handleResponse(String message) {
        System.out.println("Message:" + message);
    }

    public void closeConnection() throws Exception {
        socket.close();
    }

    public static void main(String[] args) {
        try {
            UdpClient client = new UdpClient();
            ClientConsole console = new ClientConsole(client);
            console.accept();
            client.sendToServer("hello");
        } catch (Exception e) {
            System.out.println("Error occurred :" + e.getMessage());
        }
    }
}
