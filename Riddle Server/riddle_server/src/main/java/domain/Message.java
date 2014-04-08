package main.java.domain;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 3/30/14
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Message implements Serializable {

    private String message;

    public Message(String message) {
        this.message = message;
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }

    public String getMessage() {
        return message;
    }

}
