package main.java.server;

import main.java.service.RiddleService;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 3/30/14
 * Time: 11:20 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractServer extends Thread {

    private static String commandHello = "hello";
    private static String commandRiddle = "riddle";
    private static String commandAnswer = "answer";
    private static String commandBye = "bye";
    private static String defaultOutput = "unknown message";
    private static String correctOutput = "correct";
    private static String incorrectOutput = "incorrect";

    public static RiddleService riddleService;

    public AbstractServer() {
        riddleService = new RiddleService();
        riddleService.init();
    }

    public abstract void close() throws Exception;

    protected String executeCommand(String[] cmdArr) {
        String cmd = cmdArr[0];
        try {
            if (commandHello.equals(cmd)) {
                return String.valueOf(riddleService.getSize());
            } else if(commandRiddle.equals(cmd)) {
                return executeRiddleCommand(cmdArr);
            } else if(commandAnswer.equals(cmd)) {
                return executeAnswerCommand(cmdArr);
            } else if (commandBye.equals(cmd)) {
                close();
                return "Disconnected from server";
            } else {
                return defaultOutput;
            }
        } catch (Exception ex) {
            return defaultOutput;
        }
    }

    private String executeRiddleCommand(String[] cmdArr) {
        if(cmdArr.length == 1) {
            int randomNum = new Random().nextInt(riddleService.getSize() - 1);
            return randomNum + "- " + riddleService.get(randomNum).getQuestion();
        } else if(cmdArr.length == 2) {
            int num = Integer.parseInt(cmdArr[1]);
            return num + "- " + riddleService.get(num).getQuestion();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private String executeAnswerCommand(String[] cmdArr) {
        if(cmdArr.length == 3) {
            int number = Integer.parseInt(cmdArr[1]);
            String answer = cmdArr[2];
            if(riddleService.isCorrect(number, answer)) {
                return correctOutput;
            } else {
                return incorrectOutput;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

}
