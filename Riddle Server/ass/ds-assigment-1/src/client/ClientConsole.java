package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClientConsole {

    private AbstractClient client;

    public ClientConsole(AbstractClient client) {
        try {
            this.client = client;
        } catch(Exception exception) {
            System.out.println("Error: Can't setup connection!"
                    + " Terminating client.");
            exception.printStackTrace();
            System.exit(1);
        }
    }

    public void onMessage(String message) {
        try {
            client.sendToServer(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void accept() {
        try {
            BufferedReader fromConsole =
                    new BufferedReader(new InputStreamReader(System.in));
            String message;

            while (true) {
                message = fromConsole.readLine();
                if (message.equals("bye")) {
                    client.sendToServer(message);
                    client.closeConnection();
                    System.exit(0);
                }
                onMessage(message);
            }
        } catch (Exception ex) {
            System.out.println("Unexpected error while reading from console!");
        }
    }
}
