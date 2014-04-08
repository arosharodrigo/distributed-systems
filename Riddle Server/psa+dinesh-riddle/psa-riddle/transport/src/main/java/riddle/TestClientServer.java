package riddle;

import riddle.tcp.client.RiddleClient;
import riddle.tcp.server.RiddleServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: prabath
 */
public class TestClientServer {

    public static void main(String[] args) {
        final RiddleServer riddleServer = new RiddleServer();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        riddleServer.initServer();
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
            Thread.sleep(1000);
            ClientEmulator clientEmulator = new ClientEmulator(new RiddleClient());
            Thread th = new Thread(clientEmulator);
            th.start();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static class ClientEmulator implements Runnable {

        private RiddleClient riddleClient;

        private ClientEmulator(RiddleClient riddleClient) {
            this.riddleClient = riddleClient;
        }

        @Override
        public void run() {
            riddleClient.startClient();
            for (int i = 0; i < 1; i++) {
//                riddleClient.send("Test Client : " + i);
            }
        }
    }
}
