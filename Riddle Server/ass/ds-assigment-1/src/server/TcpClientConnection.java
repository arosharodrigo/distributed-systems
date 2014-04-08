package server;

import domain.Riddle;
import repository.RiddleRepository;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.StringTokenizer;

public class TcpClientConnection extends AbstractServer {

    private Socket clientSocket;

    private BufferedReader input;

    private PrintWriter output;

    private boolean readyToStop;


    protected TcpClientConnection(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        clientSocket.setSoTimeout(0);

        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()), 1);
            output = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException ex)  {
            try {
                closeAll();
            } catch (Exception ignored) { }
            throw ex;
        }

        readyToStop = false;
        start();
    }

    public void sendToClient(String msg) throws Exception {
        if (clientSocket == null || output == null || msg == null) {
            throw new SocketException("socket does not exist");
        }

        output.write(msg + "#");
        output.flush();
    }

    public void close() throws Exception {
        readyToStop = true;
        closeAll();
    }

    public void run() {
        try {
            while (!readyToStop) {
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
                if(!readyToStop) {
                    String resp = handleMessageFromClient(sb.toString());
                    sendToClient(resp);
                }
            }
        } catch (Exception exception) {
            if (!readyToStop) {
                try {
                    closeAll();
                } catch (Exception ex) { }
            }
        }
    }

    private void closeAll() throws IOException {
        try {
            if (clientSocket != null)
                clientSocket.close();

            if (output != null)
                output.close();

            if (input != null)
                input.close();
        }
        finally {
            output = null;
            input = null;
            clientSocket = null;
        }
    }

    protected void finalize() {
        try {
            closeAll();
        } catch(IOException e) {}
    }
}
