package main.java.domain;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 3/30/14
 * Time: 9:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Riddle {

    String question;

    String answer;

    public Riddle(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
