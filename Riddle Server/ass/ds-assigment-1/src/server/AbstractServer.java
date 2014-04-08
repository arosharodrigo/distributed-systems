package server;

import repository.RiddleRepository;

import java.util.Random;
import java.util.StringTokenizer;

public abstract class AbstractServer extends Thread {
    private RiddleRepository repository;

    public AbstractServer() {
        super();
        repository = new RiddleRepository();
    }

    public abstract void close() throws Exception;

    protected String handleMessageFromClient(String message) {
        System.out.println("Message :" + message);
        try {

            if (null == message || message.trim().equals("")) {
                return "Unknown message";
            } else if(message.startsWith("hello")) {
                return handleHelloMessage(message);
            } else if(message.startsWith("riddle")) {
                return handleRiddleMessage(message);
            } else if(message.startsWith("answer")) {
                return handleAnswerMessage(message);
            } else if(message.startsWith("bye")) {
                return handleByeMessage(message);
            }
        } catch (Exception e) {
            System.out.println("Unable to send message to client:" + e.getMessage());
            try {
                close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return "Unknown message";
    }

    private String handleHelloMessage(String msg) throws Exception {
        return Integer.toString(repository.countAllRiddles());
    }

    private String handleRiddleMessage(String msg) throws Exception {
        StringTokenizer st = new StringTokenizer(msg);
        st.nextToken();
        String riddle = "";
        if (st.hasMoreTokens()) {
            try {
                int n = new Integer(st.nextToken());
                if (n < repository.countAllRiddles()) {
                    riddle = n + ". " + repository.getRiddle(n).getQuestion();
                } else {
                    riddle = "Unknown message";
                }
            } catch (NumberFormatException e) {
                riddle = "Unknown message";
            }
        } else {
            int randomRiddleNo = new Random().nextInt(repository.countAllRiddles() -1);
            riddle = randomRiddleNo + ". " + repository.getRiddle(randomRiddleNo).getQuestion();
        }

        return riddle;
    }

    private String handleAnswerMessage(String msg) throws Exception {
        StringTokenizer st = new StringTokenizer(msg);
        String reply = "";

        if (st.countTokens() >= 3) {
            st.nextToken();
            int index = new Integer(st.nextToken());
            String answer = st.nextToken();
            if (answer.equalsIgnoreCase(repository.getRiddle(index).getAnswer())) {
                reply = "Correct";
            } else {
                reply = "Incorrect";
            }
        } else {
            reply = "Unknown message";
        }

        return reply;
    }

    private String handleByeMessage(String msg) throws Exception {
        close();
        return null;
    }

}
