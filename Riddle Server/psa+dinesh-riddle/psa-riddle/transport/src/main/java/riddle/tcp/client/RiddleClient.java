package riddle.tcp.client;

import riddle.sink.MsgSink;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author: prabath
 */
public class RiddleClient implements MsgSink {

    private String host = "localhost";

    private int port = 5555;

    private ExecutorService readExecutor;

    private SocketChannel clientSocket;

    private MsgSink msgSink;

    public void startClient() {
        readExecutor = Executors.newFixedThreadPool(2);
        readExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = SocketChannel.open(new InetSocketAddress(host, port));
                    if (clientSocket != null && clientSocket.isConnected()) {
                        clientSocket.configureBlocking(false);
                        startRead();
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }

            }
        });
    }

    private Future startRead() {
        return readExecutor.submit(new Runnable() {
            @Override
            public void run() {
                ByteBuffer bf = ByteBuffer.allocate(50);
                while (true) {
                    try {
                        int count = clientSocket.read(bf);
                        if (count > 0) {
                            bf.flip();
                            msgSink.sinkMsg(bf);
                            bf.clear();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public ByteBuffer sinkMsg(ByteBuffer buff) {
        send(buff);
        return null;
    }

    private void send(ByteBuffer buff) {
        if (clientSocket != null && clientSocket.isConnected()) {
            try {
                clientSocket.write(buff);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void setMsgSink(MsgSink msgSink) {
        this.msgSink = msgSink;
    }
}
