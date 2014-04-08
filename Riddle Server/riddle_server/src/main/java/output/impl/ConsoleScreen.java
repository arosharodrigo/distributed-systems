package main.java.output.impl;

import main.java.output.Screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 3/30/14
 * Time: 6:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConsoleScreen implements Screen {

    public String print(String out) throws IOException {
        System.out.print(String.format(out));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
}
