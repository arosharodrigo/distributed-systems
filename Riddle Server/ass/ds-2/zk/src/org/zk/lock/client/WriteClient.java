package org.zk.lock.client;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.zk.lock.LockListener;
import org.zk.lock.ReadWriteLock;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WriteClient implements Runnable {
    private final String name;
    private ReadWriteLock lock;
    private final Lock localLock = new ReentrantLock();
    private final Condition condition = localLock.newCondition();

    public WriteClient(String name, String dir) throws IOException {
        this.name = name;
        ZooKeeper zookeeper = new ZooKeeper("localhost:2181", 5000, new NullWatcher());
        lock = new ReadWriteLock(zookeeper, dir, new LockListenerImpl(), name);
    }

    public void run() {

        try {
            while (!Thread.interrupted()) {
                System.out.println("["+ name +"] acquiring write lock...");
                boolean distributedLockAcquired = lock.writeLock();
                if (!distributedLockAcquired) {
                    System.out.println("[" + name + "] waiting for write lock...");
                    localLock.lock();
                    condition.await();
                    localLock.unlock();
                }
                System.out.println("["+ name +"] write lock acquired");
                process();
                lock.unLock();
                System.out.println("["+ name +"] write lock released");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    private void process() throws InterruptedException {
        System.out.println("["+ name +"] processing");
        Thread.sleep(2000);
        System.out.println("["+ name +"] processing complete");
    }

    private class LockListenerImpl implements LockListener {

        public void lockAcquired() {
            localLock.lock();
            condition.signal();
            localLock.unlock();
        }
    }

    private class NullWatcher implements Watcher {

        public void process(WatchedEvent watchedEvent) {
            //nothing to do
        }
    }
}
