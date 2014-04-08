package riddle.service;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author: prabath
 */
public class RiddleProvider {

    private Map<Integer, String> riddleMap = new HashMap<>();

    private Map<Integer, String> answerMap = new HashMap<>();

    public void init() {
        riddleMap.put(1, "Can 1+1 be changed to 441 ?");
        answerMap.put(1, "yes");

        riddleMap.put(2, "How many characters do you have ?");
        answerMap.put(2, "3");

        riddleMap.put(3, "Can 1+1 be changed to 441 ?");
        answerMap.put(3, "yes");

        riddleMap.put(4, "How many characters do you have ?");
        answerMap.put(4, "3");

        riddleMap.put(5, "Can 1+1 be changed to 441 ?");
        answerMap.put(5, "yes");

        riddleMap.put(6, "How many characters do you have ?");
        answerMap.put(6, "3");

        riddleMap.put(7, "Can 1+1 be changed to 441 ?");
        answerMap.put(7, "yes");

        riddleMap.put(8, "How many characters do you have ?");
        answerMap.put(8, "3");

    }

    public int getRiddleSize() {
        return riddleMap.size();
    }

    public String getRiddle(int index, boolean random) {
        if (random && riddleMap.size() > 0) {
            Random rndm = new Random();
            int randomIndex = Math.abs(rndm.nextInt()) % (riddleMap.size() - 1);
            String riddle = riddleMap.get(randomIndex);
            StringBuilder riddleWithIndex = new StringBuilder("");
            riddleWithIndex.
                    append(randomIndex).
                    append(" : ").
                    append(riddle);
            return riddleWithIndex.toString();
        }
        if (index > -1 && riddleMap.size() > 0) {
            return riddleMap.get(index);
        }
        return null;
    }

    public String checkRiddleAnswer(int index, String answer) {
        if (index < 0 || (answer == null || answer.isEmpty())) {
            return "incorrect";
        }
        String realAnswer = answerMap.get(index);
        if (realAnswer != null && !realAnswer.isEmpty()) {
            return answer.equals(realAnswer) ? "correct" : "incorrect";
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
