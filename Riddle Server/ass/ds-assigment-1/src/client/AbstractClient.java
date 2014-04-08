package client;

public interface AbstractClient {

    public void sendToServer(String req) throws Exception;

    public void closeConnection() throws Exception;

}
