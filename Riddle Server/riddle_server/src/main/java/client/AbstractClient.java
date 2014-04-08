package main.java.client;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 3/31/14
 * Time: 3:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractClient {

    public static int CLIENT_PORT = 8000;
    public static String SERVER_IP = "127.0.0.1";
    public static int SERVER_PORT = 7000;

    public void setConfigurations(String[] args) {
        switch(args.length) {
            case 3:
                SERVER_PORT = Integer.parseInt(args[2]);
            case 2:
                SERVER_IP = args[1];
            case 1:
                CLIENT_PORT = Integer.parseInt(args[0]);
                break;
            default:
                break;
        }
    }
}
