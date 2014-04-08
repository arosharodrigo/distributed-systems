package client;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class TcpClient implements Runnable, AbstractClient {

    private Socket clientSocket;
    private PrintWriter output;
    private BufferedReader input;
    private Thread clientReader;
    private boolean readyToStop= false;
    private String host = "localhost";
    private int port = 9080;

    public TcpClient() {
    }

    public void openConnection() throws IOException {
        if(isConnected())
            return;

        try {
            clientSocket= new Socket(host, port);
            output = new PrintWriter(clientSocket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ex) {
            try {
                closeAll();
            } catch (Exception ignored) { }

            throw ex;
        }

        clientReader = new Thread(this);
        readyToStop = false;
        clientReader.start();
    }

    public void sendToServer(String msg) throws IOException {
        if (clientSocket == null || output == null) {
            throw new SocketException("socket does not exist");
        }
        System.out.println("Sending msg to server:" + msg);
        output.write(msg + "#");
        output.flush();
    }

    public void closeConnection() throws Exception {
        readyToStop= true;
        closeAll();
    }

    public boolean isConnected() {
        return clientReader!=null && clientReader.isAlive();
    }

    public void run() {
        try {
            while(!readyToStop) {

                StringBuilder sb = new StringBuilder();
                int c = 0;
                while ((char) c != '#') {
                    c = input.read();
                    if ((char) c != '#') {
                        sb.append((char) c);
                    } else {
                        break;
                    }
                }

                if (!readyToStop) {
                    handleMessageFromServer(sb.toString());
                }
            }
        } catch (Exception exception) {
            if(!readyToStop) {
                try {
                    closeAll();
                } catch (Exception ignored) { }
                clientReader = null;
            }
        } finally {
            clientReader = null;
        }
    }

    protected void handleMessageFromServer(String msg) {
        System.out.println("Message :" + msg);
    }

    private void closeAll() throws IOException {
        try {
            if (output != null)
                output.close();

            if (input != null)
                input.close();

            if (clientSocket != null)
                clientSocket.close();
        } finally {
            output = null;
            input = null;
            clientSocket = null;
        }
    }

    public static void main(String[] args) {
        try {
            TcpClient client= new TcpClient();
            client.openConnection();
            client.sendToServer("hello");
            ClientConsole console = new ClientConsole(client);
            console.accept();
        } catch (IOException e) {
            System.out.println("Error occurred while starting server:" + e.getMessage());
        }
    }

}
