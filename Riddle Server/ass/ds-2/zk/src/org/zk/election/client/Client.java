package org.zk.election.client;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.zk.election.ElectionEventListener;
import org.zk.election.LeaderElector;

import java.io.IOException;

public class Client {

    private final String name;
    private LeaderElector leaderElector;
    private ZooKeeper zooKeeper;

    public Client(String name){
        this.name = name;

    }

    public void start() throws IOException, InterruptedException, KeeperException {
        System.out.println("[" + name + "] starting");
        zooKeeper = new ZooKeeper("localhost:2181", 5000, new NullWatcher());
        leaderElector = new LeaderElector(zooKeeper, "/election", new ElectionEventListenerImpl(), name);
        leaderElector.start();
    }

    public void stop() throws InterruptedException {
        System.out.println("[" + name + "] stopping");
        zooKeeper.close();
    }

    private class NullWatcher implements Watcher {

        public void process(WatchedEvent watchedEvent) {
            //nothing to do
        }
    }

    private class ElectionEventListenerImpl implements ElectionEventListener {

        public void onLeaderChanged(String newLeaderPath) {
            System.out.println("[" + name +"] Notified leader change -" + newLeaderPath);
        }

        public void onElected() {
            System.out.println("[" + name +"] Elected as leader");
        }
    }

    public static void main(String[] args) throws InterruptedException {

        new Thread(new Runnable() {
            public void run() {
                while (!Thread.interrupted()) {
                try {
                    Client client = new Client("P01");
                    client.start();
                    Thread.sleep(4000);
                    client.stop();
                    Thread.sleep(3000);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
                }

            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                while (!Thread.interrupted()) {
                try {
                    Client client = new Client("P02");
                    client.start();
                    Thread.sleep(2000);
                    client.stop();
                    Thread.sleep(3000);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
                }

            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                while (!Thread.interrupted()) {
                try {
                    Client client = new Client("P03");
                    client.start();
                    Thread.sleep(2000);
                    client.stop();
                    Thread.sleep(1000);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
                }

            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                while (!Thread.interrupted()) {
                try {
                    Client client = new Client("P04");
                    client.start();
                    Thread.sleep(2000);
                    client.stop();
                    Thread.sleep(1000);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
                }

            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                System.out.println("p05");
                while (!Thread.interrupted()) {
                try {
                    Client client = new Client("P05");
                    client.start();
                    Thread.sleep(2000);
                    client.stop();
                    Thread.sleep(1000);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
                }

            }
        }).start();

        System.out.println("=================");
    }

}
