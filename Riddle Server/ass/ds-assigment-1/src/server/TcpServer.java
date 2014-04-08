package server;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer implements Runnable {

    private ServerSocket serverSocket = null;
    private Thread connectionListener = null;
    private int port;
    private int timeout = 500;
    private int backlog = 10;
    private boolean readyToStop = true;


    public void listen() throws IOException {
        serverSocket = new ServerSocket(port, backlog);

        serverSocket.setSoTimeout(timeout);

        connectionListener = new Thread(this);
        connectionListener.start();
    }

    public void stopListening() {
        readyToStop = true;
    }

    public void run() {
        readyToStop= false;

        try {
            while(!readyToStop) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    synchronized(this) {
                        if (!readyToStop)
                        {
                            new TcpClientConnection(clientSocket);
                        }
                    }
                }catch (InterruptedIOException exception) {
                }
            }
        } catch (IOException exception) {
        } finally {
            readyToStop = true;
            connectionListener = null;
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public static void main(String[] args) {
        TcpServer sv = new TcpServer();
        sv.setPort(9080);
        try {
            sv.listen();
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }
    }
}
