package riddle.udp.client;

import riddle.sink.MsgSink;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author: prabath
 */
public class RiddleClientUdp implements MsgSink {

    private String host = "localhost";

    private int port = 5555;

    private ExecutorService readExecutor;

    private DatagramSocket clientSocket;

    private DatagramChannel clientChannel;

    private MsgSink msgSink;

    private Long id;

    public void startClient() {
        readExecutor = Executors.newFixedThreadPool(2);
        id = System.nanoTime();
        readExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    clientChannel = DatagramChannel.open().connect(new InetSocketAddress(host, port));
                    clientChannel.configureBlocking(false);
                    clientSocket = clientChannel.socket();
                    if (clientSocket != null && clientSocket.isConnected()) {
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
                        if (clientChannel.receive(bf) != null) {
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
        ByteBuffer buffWithId = ByteBuffer.allocate(50);
        buffWithId.putLong(id).put(buff);
        send((ByteBuffer) buffWithId.flip());
        return null;
    }

    private void send(ByteBuffer buff) {
        if (clientSocket != null && clientSocket.isConnected()) {
            try {
                clientChannel.write(buff);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void setMsgSink(MsgSink msgSink) {
        this.msgSink = msgSink;
    }
}
