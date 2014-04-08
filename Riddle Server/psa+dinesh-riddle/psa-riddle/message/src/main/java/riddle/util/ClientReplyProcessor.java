package riddle.util;

import riddle.service.ChannelChangeListener;

import java.nio.ByteBuffer;

/**
 * @author: prabath
 */
public class ClientReplyProcessor implements ReplyProcessor {

    private ChannelChangeListener changeListerner;

    @Override
    public void registerListerner(ChannelChangeListener listener) {
        changeListerner = listener;
    }

    @Override
    public ByteBuffer createReply(ByteBuffer buffer) {
        String reply = getMsg(buffer);
        System.out.println(reply);
        if (reply.equals(Command.BYE) && changeListerner != null) {
            changeListerner.notifyChannelClose();
        }
        return null;
    }

    private String getMsg(ByteBuffer buffer) {
        buffer = buffer.slice();
        byte[] msgArr = new byte[buffer.limit()];
        buffer.get(msgArr, 0, buffer.limit());
        String msg = new String(msgArr);
        return msg;
    }

}
