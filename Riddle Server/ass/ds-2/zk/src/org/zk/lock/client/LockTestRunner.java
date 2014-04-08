package org.zk.lock.client;

import java.io.IOException;

public class LockTestRunner {

    private static final String dir = "/lock";

    public static void main(String[] args) throws IOException, InterruptedException {
        WriteClient wc1 = new WriteClient("WC1", dir);
        WriteClient wc2 = new WriteClient("WC2", dir);
        ReadClient rc1 = new ReadClient("RC1", dir);
        ReadClient rc2 = new ReadClient("RC2", dir);
        ReadClient rc3 = new ReadClient("RC3", dir);

        new Thread(wc1).start();
        new Thread(wc2).start();
        new Thread(rc1).start();
        new Thread(rc2).start();
        new Thread(rc3).start();

    }
}
