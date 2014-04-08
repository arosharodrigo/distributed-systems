package riddle.util;

import riddle.service.ChannelChangeListener;
import riddle.service.RiddleProvider;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

import static riddle.util.Command.*;

/**
 * @author: prabath
 */
public class ServerReplyProcessor implements ReplyProcessor {

    private ConcurrentHashMap<Long, Boolean> registerMap;

    private int numberOfRiddles = 10;

    private RiddleProvider riddleProvider;

    private ChannelChangeListener changeListerner;

    public ServerReplyProcessor() {
        this.registerMap = new ConcurrentHashMap<>();
        this.riddleProvider = new RiddleProvider();
        riddleProvider.init();
    }

    @Override
    public void registerListerner(ChannelChangeListener listerner) {
        changeListerner = listerner;
    }

    @Override
    public ByteBuffer createReply(ByteBuffer buffer) throws IOException {
        if (buffer.limit() > 8) {
            buffer = buffer.slice();
            Long id = getId(buffer);
            String msg = getMsg(buffer);
            printInMsg(msg);
            return actOnMsg(id, msg);
        }
        return ByteBuffer.wrap("unknown message".getBytes());
    }

    private ByteBuffer actOnMsg(Long id, String msg) {
        String[] paramList = msg.split(" ");
        if (paramList.length > 0) {
            String command = paramList[0];
            switch (command) {
                case HELLO:
                    return actOnHello(id, paramList);
                case RIDDLE:
                    return actOnRiddle(id, paramList);
                case ANSWER:
                    return actOnAnswer(id, paramList);
                case BYE:
                    return actOnBye(id, paramList);
                default:
                    return ByteBuffer.wrap("unknown message".getBytes());
            }
        }
        return ByteBuffer.wrap("unknown message".getBytes());
    }

    private ByteBuffer actOnBye(Long id, String[] paramList) {
        try {
            if (id == null || !registerMap.containsKey(id)) {
                return ByteBuffer.wrap("unknown message".getBytes());
            }
            registerMap.remove(id);
            return ByteBuffer.wrap("bye".getBytes());

        } catch (Exception ex) {
            return ByteBuffer.wrap("unknown message".getBytes());
        } finally {
            if (changeListerner != null) {
                changeListerner.notifyChannelClose();
            }
        }

    }

    private ByteBuffer actOnAnswer(Long id, String[] paramList) {
        try {
            if (id == null || !registerMap.containsKey(id)) {
                return ByteBuffer.wrap("unknown message".getBytes());
            }
            if (paramList.length > 2) {
                int ansIndex = Integer.parseInt(paramList[1]);
                StringBuilder answerBuff = new StringBuilder("");
                for (int i = 2; i < paramList.length; i++) {
                    answerBuff.append(paramList[i]);
                }
                String answer = answerBuff.toString().trim();
                String correctness = riddleProvider.checkRiddleAnswer(ansIndex, answer);

                return ByteBuffer.wrap((correctness).getBytes());
            } else {
                return ByteBuffer.wrap("incorrect".getBytes());
            }

        } catch (Exception ex) {
            return ByteBuffer.wrap("unknown message".getBytes());
        }

    }

    private ByteBuffer actOnRiddle(Long id, String[] paramList) {
        try {
            if (id == null || !registerMap.containsKey(id)) {
                return ByteBuffer.wrap("unknown message".getBytes());
            }
            if (paramList.length > 1) {
                int index = Integer.parseInt(paramList[1]);
                return getRiddle(index, false);
            } else {
                return getRiddle(-1, true);
            }
        } catch (Exception ex) {
            return ByteBuffer.wrap("unknown message".getBytes());
        }

    }

    private ByteBuffer actOnHello(Long id, String[] paramList) {
        ByteBuffer reply = ByteBuffer.wrap("unknown message".getBytes());
        if (id != null) {
            reply = ByteBuffer.wrap(String.valueOf(numberOfRiddles).getBytes());
            if (!registerMap.containsKey(id)) {
                registerMap.put(id, true);
            }
        }
        return reply;
    }

    private ByteBuffer getRiddle(int index, boolean random) {
        String riddle = riddleProvider.getRiddle(index, random);
        if (riddle != null) {
            return ByteBuffer.wrap(riddle.getBytes());
        }
        return ByteBuffer.wrap("No riddles available".getBytes());
    }

    private void printInMsg(String msg) throws IOException {
        System.out.println(" " + msg);
    }

    private String getMsg(ByteBuffer buffer) {
        buffer = buffer.slice();
        byte[] msgArr = new byte[buffer.limit()];
        buffer.get(msgArr, 0, buffer.limit());
        String msg = new String(msgArr);
        return msg;
    }

    private Long getId(ByteBuffer buffer) {
        byte[] idArr = new byte[8];
        buffer.get(idArr, 0, 8);
        ByteBuffer idBuff = ByteBuffer.wrap(idArr);
        Long id = idBuff.getLong();
        return id;
    }
}
