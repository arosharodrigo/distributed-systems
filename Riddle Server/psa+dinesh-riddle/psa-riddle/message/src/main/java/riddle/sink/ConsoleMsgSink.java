package riddle.sink;

import riddle.util.ReplyProcessor;

import java.nio.ByteBuffer;

/**
 * @author: prabath
 */
public class ConsoleMsgSink implements MsgSink {

    private String tag = "MSG : ";

    private ReplyProcessor replyProcessor;

    @Override
    public ByteBuffer sinkMsg(ByteBuffer inBuff) {
        ByteBuffer reply = null;
        try {
            reply = replyProcessor.createReply(inBuff);
            return reply;
        } catch (Exception e) {
            System.err.println(e);
        }
        return reply;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setReplyProcessor(ReplyProcessor replyProcessor) {
        this.replyProcessor = replyProcessor;
    }
}
