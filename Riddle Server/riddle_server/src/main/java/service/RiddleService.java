package main.java.service;

import main.java.domain.Riddle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 3/30/14
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class RiddleService {

    private List<Riddle> riddles;

    public void init() {
        riddles = new ArrayList<Riddle>();
        riddles.add(new Riddle("How can you add eight 8's to get the number 1,000?", "888+88+8+8+8"));
        riddles.add(new Riddle("What is the center of gravity?", "v"));
        riddles.add(new Riddle("Who was buried in Grant's Tomb?", "Grant"));
        riddles.add(new Riddle("How many seconds are in a year?", "12"));
        riddles.add(new Riddle("What can you catch but not throw?", "cold"));
        riddles.add(new Riddle("What goes up and never comes down?", "age"));
    }

    public int getSize() {
        return riddles.size();
    }

    public Riddle get(int number) {
        return riddles.get(number - 1);
    }

    public boolean isCorrect(int number, String answer) {
        if (number <= riddles.size()) {
            return riddles.get(number - 1).getAnswer().equals(answer);
        } else {
            return false;
        }
    }

}
